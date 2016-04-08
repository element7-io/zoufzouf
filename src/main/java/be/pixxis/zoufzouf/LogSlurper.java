package be.pixxis.zoufzouf;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.*;

/**
 * @author Gert Leenders
 * @version $Id$
 */
public class LogSlurper {
    //TODO: make pricing collection from http://aws.amazon.com/cloudfront/pricing/

    //TODO: re-enable mailing
    //TODO: re-enable daily measurement

    //TODO: Capture unprocessed lines in an error file based on the number of fields (error-17.log, error-18.log,....)

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

    public boolean isDryRun() {
        return dryRun;
    }

    private boolean dryRun = false;
    private long processedLines = 0;

    public static void main(final String[] args) {
        final LogSlurper logSlurper = new LogSlurper();
        logSlurper.init();
        logSlurper.avoidConcurrentApplicationRuns();
        logSlurper.analyze();
    }

    /**
     * Move a file from one folder to another.
     *
     * @param blobStore         the blob store
     * @param sourceKey         the file source location
     * @param destinationFolder the destination folder
     * @return the destination key of the moved file
     * @throws Exception
     */
    @GuardedBy("ThreadLocal blobStore")
    public static String moveFile(final BlobStore blobStore, final String sourceKey,
                                  final String destinationFolder) throws KeyNotFoundException {

        final Blob blob = blobStore.getBlob(BUCKET, sourceKey);

        if (blob != null) {

            final String destinationKey = destinationFolder + "/" + sourceKey.substring(sourceKey.lastIndexOf('/') + 1);

            blob.getMetadata().setName(destinationKey);
            blobStore.putBlob(BUCKET, blob);
            blobStore.removeBlob(BUCKET, sourceKey);
            return destinationKey;

        } else {

            throw new KeyNotFoundException(sourceKey, BUCKET, "Error while moving file.");

        }

    }

    private void init() {

        final String filename = "config.properties";
        final InputStream resourceAsStream = LogSlurper.class.getClass().getResourceAsStream("/" + filename);

        if (resourceAsStream != null) {
            final Properties properties = new Properties();
            try {
                properties.load(resourceAsStream);

                if (Boolean.valueOf(properties.getProperty("dry_run"))) {
                    dryRun = true;
                }

                blobStoreContext = ContextBuilder.newBuilder("aws-s3").credentials(properties.getProperty
                    ("AWS_ACCESS_KEY_ID"), properties.getProperty("AWS_SECRET_ACCESS_KEY"))
                    .buildView(BlobStoreContext.class);

            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
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
            System.exit(0);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Analyze the logs in the 'misc.pixxis.be' bucket.
     * The logs files to analyse are taken form the '/logs' folder and moved to the 'logs/processing' folder before
     * processing.
     * When the log file is processed it is moved from the 'logs/processing' folder to the 'logs/processed' folder.
     */
    public void analyze() {

        try {

            final long startTime = System.currentTimeMillis();

            LOG.info("Version 1.1,build_111");
            LOG.info("Log analyzer started on bucket: {}", BUCKET);
            LOG.info("Threads: {}, Concurrency level: {}", NUMBER_OF_THREADS, CONCURRENCY_LEVEL);
            LOG.info("Batch size: {}", NUMBER_OF_FILES_TO_PROCESS);

            // Create a new MongoDB connection.
            //MongoBean.INSTANCE.init()

            // Create Container
            final BlobStore blobStore = blobStoreContext.getBlobStore();

            final ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
            final List<LogSlurperThread<Void>> tasks = new ArrayList<>();

            int i = 0;

            // Cleanup the logs-processing folder if necessary.
            for (StorageMetadata resourceMd : blobStore.list(BUCKET, inDirectory(LOGS_PROCESSING_FOLDER).maxResults
                (1000))) {

                if (resourceMd.getType() == StorageType.BLOB) {
                    LOG.trace("file: {} added to que for processing.", resourceMd.getName());
                    tasks.add(new LogSlurperThread<>(this, resourceMd.getName()));
                    i++;
                }
            }

            if (i > 0) {

                LOG.info("{} tasks added for processing from {} folder", i, LOGS_PROCESSING_FOLDER);

            } else {

                // Get the logs form the logs folder
                for (StorageMetadata resourceMd : blobStore.list(BUCKET, inDirectory(LOGS_FOLDER).maxResults
                    (NUMBER_OF_FILES_TO_PROCESS))) {

                    if (resourceMd.getType() == StorageType.BLOB) {

                        LOG.trace("file: {} added to que for processing.", resourceMd.getName());

                        if (this.dryRun) {
                            tasks.add(new LogSlurperThread<>(this, resourceMd.getName()));
                        } else {
                            final String processingKey = moveFile(blobStore, resourceMd.getName(),
                                LOGS_PROCESSING_FOLDER);
                            tasks.add(new LogSlurperThread<>(this, processingKey));
                        }
                        i++;
                    }
                }
                LOG.info("{} tasks added for processing from {} folder", i, LOGS_FOLDER);

            }

            pool.invokeAll(tasks);
            pool.shutdown();

            final long endTime = System.currentTimeMillis();
            final long durationTime = endTime - startTime;

            final String duration = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(durationTime),
                TimeUnit.MILLISECONDS.toSeconds(durationTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTime))
            );

            LOG.info("{} lines processed", processedLines);
            LOG.info("Finished, duration {}", duration);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            // Close connection
            blobStoreContext.close();
            //MongoBean.INSTANCE.shutdown()
            System.exit(0);
        }
    }

    @GuardedBy("Synchronized")
    synchronized void increaseProcessedLines(final long processedLines) {
        this.processedLines += processedLines;
    }
}
