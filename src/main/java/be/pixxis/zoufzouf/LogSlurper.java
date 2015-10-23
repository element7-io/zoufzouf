package be.pixxis.zoufzouf;

import net.jcip.annotations.GuardedBy;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.g

/**
 * @author glnd
 * @version $Id$
 */
public class LogSlurper {
    //TODO: monthly job (2de of the month), price calculation for last month
    //TODO: Daily job, clean days older then 30 days, look for threshold exceeds
    //TODO: make pricing collection from http://aws.amazon.com/cloudfront/pricing/

    //TODO: re-enable mailing
    //TODO: re-enable daily measurement

    //TODO: Capture unprocessed lines in an error file based on the number of fields (error-17.log, error-18.log,....)

    private final Logger LOG = LoggerFactory.getLogger(LogSlurper.class);

    private final static int NUMBER_OF_THREADS = 8;
    private static final int CONCURRENCY_LEVEL = NUMBER_OF_THREADS / 4
    private final static int NUMBER_OF_FILES_TO_PROCESS = 1000;

    private static final String ACCESS_KEY = "...";
    private static final String SECRET_KEY = "xxx";

    public static final String BUCKET = "misc.example.com";

    private static final String LOGS_FOLDER = "logs/cloudfront";
    private static final String LOGS_PROCESSING_FOLDER = "logs-processing/cloudfront";

    private long processedLines = 0;

    public static BlobStoreContext blobStoreContext = ContextBuilder.newBuilder("aws-s3").credentials(ACCESS_KEY, SECRET_KEY)
            .buildView(BlobStoreContext.class);

    public static final ConcurrentHashMap<String, String> localCache = new ConcurrentHashMap<String, String>(16, 0.9f, CONCURRENCY_LEVEL);

    private ServerSocket ss;

    static void main(String[] args) {
        final LogSlurper logSlurper = new LogSlurper();
        logSlurper.avoidConcurrentApplicationRuns();
        logSlurper.analyze();
    }


    /**
     * Prevent to have multiple analyzers running at the same time.
     */
    private void avoidConcurrentApplicationRuns() {
        try {
            LOG.info("-----> New Analyzer Run....");
            LOG.info("Check server socket binding on port 5353.");
            ss = new ServerSocket();
            ss.bind(new InetSocketAddress(5353));
            LOG.info("No other application instance running. Proceeding...");
        }
        catch (SocketException se) {
            LOG.info("Application run aborted, other application instance already running!");
            System.exit(0);
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }
    }


    /**
     * Analyze the logs in the 'cdn.parleys.com' bucket.
     * The logs files to analyse are taken form the '/logs' folder and moved to the 'logs/processing' folder before processing.
     * When the log file is processed it is moved from the 'logs/processing' folder to the 'logs/processed' folder.
     */
    void analyze() {

        try {

            long startTime = System.currentTimeMillis();

            LOG.info("Version 1.1,build_111")
            LOG.info("Log analyzer started on bucket: {}", BUCKET)
            LOG.info("Threads: {}, Concurrency level: {}", NUMBER_OF_THREADS, CONCURRENCY_LEVEL)
            LOG.info("Batch size: {}", NUMBER_OF_FILES_TO_PROCESS)

            // Create a new MongoDB connection.
            MongoBean.INSTANCE.init()

            // Create Container
            final BlobStore blobStore = blobStoreContext.getBlobStore()

            final ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
            final List<AnalyzerThread<Void>> tasks = new ArrayList<AnalyzerThread<Void>>();

            int i = 0

            // Cleanup the logs-processing folder if necessary.
            for (StorageMetadata resourceMd in blobStore.list(BUCKET,
                    inDirectory(LOGS_PROCESSING_FOLDER).maxResults(1000))) {

                if (resourceMd.getType() == StorageType.BLOB) {
                    LOG.trace("file: {} added to que for processing.", resourceMd.getName())
                    tasks.add(new AnalyzerThread<Void>(this, resourceMd.getName()))
                    i++
                }
            }

            if (i > 0) {

                LOG.info("{} tasks added for processing from {} folder", i, LOGS_PROCESSING_FOLDER)

            } else {

                // Get the logs form the logs folder
                for (StorageMetadata resourceMd in blobStore.list(BUCKET,
                        inDirectory(LOGS_FOLDER).maxResults(NUMBER_OF_FILES_TO_PROCESS))) {

                    if (resourceMd.getType() == StorageType.BLOB) {

                        LOG.trace("file: {} added to que for processing.", resourceMd.getName())
                        def String processingKey = moveFile(blobStore, resourceMd.getName(), LOGS_PROCESSING_FOLDER)
                        tasks.add(new AnalyzerThread<Void>(this, processingKey))
                        i++
                    }
                }
                LOG.info("{} tasks added for processing from {} folder", i, LOGS_FOLDER)

            }

            pool.invokeAll(tasks)
            pool.shutdown();

            long endTime = System.currentTimeMillis()
            long durationTime = endTime - startTime

            String duration = String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(durationTime),
                    TimeUnit.MILLISECONDS.toSeconds(durationTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTime))
            );

            LOG.info("{} lines processed", processedLines)
            LOG.info("Finished, duration {}", duration)

        }

        catch (Exception e) {
            LOG.error(e.getMessage(), e)
        }
        finally {
            // Close connection
            blobStoreContext.close();
            MongoBean.INSTANCE.shutdown()
            System.exit(0);
        }
    }

    /**
     * Move a file from one folder to another.
     * @param blobStore the blob store
     * @param sourceKey the file source location
     * @param destinationFolder the destination folder
     * @return the destination key of the moved file
     * @throws Exception
     */
    @GuardedBy("ThreadLocal blobStore")
    public static String moveFile(def final BlobStore blobStore, final String sourceKey,
                                  final String destinationFolder) throws Exception {

        final Blob blob = blobStore.getBlob(BUCKET, sourceKey)

        if (blob != null) {

            final String destinationKey = destinationFolder + "/" + sourceKey.substring(sourceKey.lastIndexOf('/') + 1)

            blob.metadata.name = destinationKey
            blobStore.putBlob(BUCKET, blob)
            blobStore.removeBlob(BUCKET, sourceKey)
            return destinationKey;

        } else {

            throw new Exception("Can't move file, " + sourceKey + " Not found.")

        }

    }

    @GuardedBy("Synchronized")
    synchronized void increaseProcessedLines(final long processedLines) {
        this.processedLines += processedLines
    }
}
