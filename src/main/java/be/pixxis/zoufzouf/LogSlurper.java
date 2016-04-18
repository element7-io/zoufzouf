package be.pixxis.zoufzouf;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.inDirectory;

import be.pixxis.zoufzouf.persistence.MongoBean;
import net.jcip.annotations.GuardedBy;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Cloudfront log slurper.
 *
 * @author Gert Leenders
 * @version $Id$
 */
public class LogSlurper {
  //TODO: make pricing collection from http://aws.amazon.com/cloudfront/pricing/

  //TODO: re-enable mailing
  //TODO: re-enable daily measurement

  //TODO: Capture unprocessed lines in an error file based on the number of fields (error-17
  // .log, error-18.log,....)

  static final String BUCKET = "cf.pixxis.be";
  private static final Logger LOG = LoggerFactory.getLogger(LogSlurper.class);
  private static final int NUMBER_OF_THREADS = 8;
  private static final int CONCURRENCY_LEVEL = NUMBER_OF_THREADS / 4;
  static final ConcurrentHashMap<String, String> LOCAL_CACHE =
      new ConcurrentHashMap<String, String>(16, 0.9f, CONCURRENCY_LEVEL);
  private static final int NUMBER_OF_FILES_TO_PROCESS = 1000;
  private static final String LOGS_FOLDER = "logs/cloudfront";
  private static final String LOGS_PROCESSING_FOLDER = "logs-processing/cloudfront";
  static BlobStoreContext blobStoreContext;
  private Configuration config;
  private long processedLines = 0;

  /**
   * Main method to start the log slurper.
   *
   * @param args start-up parameters
   */
  public static void main(final String[] args) {
    final LogSlurper logSlurper = new LogSlurper();
    logSlurper.init();
    logSlurper.avoidConcurrentApplicationRuns();
    logSlurper.analyze();
  }

  /**
   * Move a file from one AWS S3 folder to another.
   *
   * @param blobStore         the blob store
   * @param sourceKey         the file source location
   * @param destinationFolder the destination folder
   * @return the destination key of the moved file
   * @throws KeyNotFoundException if a key is not found
   */
  @GuardedBy("ThreadLocal blobStore")
  private String moveFile(final BlobStore blobStore, final String sourceKey,
                          final String destinationFolder) throws KeyNotFoundException {

    final Blob blob = blobStore.getBlob(BUCKET, sourceKey);

    if (blob != null) {

      final String destinationKey = destinationFolder + "/"
          + sourceKey.substring(sourceKey.lastIndexOf('/') + 1);

      blob.getMetadata().setName(destinationKey);
      blobStore.putBlob(BUCKET, blob);
      blobStore.removeBlob(BUCKET, sourceKey);
      return destinationKey;

    } else {
      throw new KeyNotFoundException(sourceKey, BUCKET, "Error while moving file.");
    }
  }

  public Configuration getConfig() {
    return config;
  }

  private void init() {


    try (
        final InputStream yamlStream =
            LogSlurper.class.getClass().getResourceAsStream("/properties.yml")) {

      if (yamlStream != null) {

        final Yaml yaml = new Yaml();
        config = yaml.loadAs(yamlStream, Configuration.class);
        LOG.info("Yaml configuration file initialized.");

        // Use Docker container links if environment variables exists
        final Map<String, String> env = System.getenv();
        final String mongoLink = env.get("MONGO_PORT_27017_TCP");
        if (mongoLink != null) {
          config.setFromEnv(mongoLink);
          LOG.info("Using Docker mongo Link");
        }

        blobStoreContext = ContextBuilder.newBuilder("aws-s3").credentials(config.getAwsAccessKey(),
            config.getAwsSecretKey()).buildView(BlobStoreContext.class);
      }
    } catch (IOException ie) {
      LOG.error(ie.getMessage());
      throw new RuntimeException(ie.getMessage());
    }
  }

  /**
   * Prevent to have multiple analyzers running at the same time.
   */
  private void avoidConcurrentApplicationRuns() {
    try {
      LOG.info("-----> New Analyzer Run....");
      LOG.info("Check server socket binding on port 5353.");
      final ServerSocket ss = new ServerSocket();
      ss.bind(new InetSocketAddress(5353));
      LOG.info("No other application instance running. Proceeding...");
    } catch (SocketException se) {
      LOG.info("Application run aborted, other application instance already running!");
      throw new RuntimeException("Application run aborted, other application instance already "
          + "running!");
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      throw new RuntimeException(ex.getMessage());
    }
  }

  /**
   * Analyze the logs in the 'misc.pixxis.be' bucket.
   * The logs files to analyse are taken form the '/logs' folder and moved to the 'logs/processing'
   * folder before  processing.
   * When the log file is processed it is moved from the 'logs/processing' folder to the
   * 'logs/processed' folder.
   */
  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("IS2_INCONSISTENT_SYNC")
  public void analyze() {

    try {

      final long startTime = System.currentTimeMillis();

      LOG.info("Version 1.1,build_111");
      LOG.info("Log analyzer started on bucket: {}", BUCKET);
      LOG.info("Threads: {}, Concurrency level: {}", NUMBER_OF_THREADS, CONCURRENCY_LEVEL);
      LOG.info("Batch size: {}", NUMBER_OF_FILES_TO_PROCESS);

      // Create a new MongoDB connection
      MongoBean.INSTANCE.init(config.getServerAddresses());

      // Create Container
      final BlobStore blobStore = blobStoreContext.getBlobStore();

      final ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
      final List<LogSlurperThread<Void>> tasks = new ArrayList<>();

      int counter = 0;

      // Cleanup the logs-processing folder if necessary.
      for (StorageMetadata resourceMd : blobStore.list(BUCKET,
          inDirectory(LOGS_PROCESSING_FOLDER).maxResults(1000))) {

        if (resourceMd.getType() == StorageType.BLOB) {
          LOG.trace("file: {} added to que for processing.", resourceMd.getName());
          tasks.add(new LogSlurperThread<>(this, resourceMd.getName()));
          counter++;
        }
      }

      if (counter > 0) {

        LOG.info("{} tasks added for processing from {} folder", counter, LOGS_PROCESSING_FOLDER);

      } else {

        // Get the logs form the logs folder
        for (StorageMetadata resourceMd :
            blobStore.list(BUCKET, inDirectory(LOGS_FOLDER)
                .maxResults(NUMBER_OF_FILES_TO_PROCESS))) {

          if (resourceMd.getType() == StorageType.BLOB) {

            LOG.trace("file: {} added to que for processing.", resourceMd.getName());

            if (this.config.isDryRun()) {
              tasks.add(new LogSlurperThread<>(this, resourceMd.getName()));
            } else {
              final String processingKey = moveFile(blobStore, resourceMd.getName(),
                  LOGS_PROCESSING_FOLDER);
              tasks.add(new LogSlurperThread<>(this, processingKey));
            }
            counter++;
          }
        }
        LOG.info("{} tasks added for processing from {} folder", counter, LOGS_FOLDER);
      }

      pool.invokeAll(tasks);
      pool.shutdown();

      final long endTime = System.currentTimeMillis();
      final long durationTime = endTime - startTime;

      final String duration = String.format("%d min, %d sec",
          TimeUnit.MILLISECONDS.toMinutes(durationTime),
          TimeUnit.MILLISECONDS.toSeconds(durationTime)
              - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTime))
      );

      LOG.info("{} lines processed", this.processedLines);
      LOG.info("Finished, duration {}", duration);

    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
    } finally {
      // Close connection
      blobStoreContext.close();
      MongoBean.INSTANCE.shutdown();
    }
  }

  @GuardedBy("Synchronized")
  synchronized void increaseProcessedLines(final long processedLines) {
    this.processedLines += processedLines;
  }
}
