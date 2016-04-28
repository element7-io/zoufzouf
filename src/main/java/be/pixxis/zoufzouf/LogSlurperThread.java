package be.pixxis.zoufzouf;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import be.pixxis.zoufzouf.location.EdgeLocation;
import be.pixxis.zoufzouf.location.PricingRegionNotFound;
import be.pixxis.zoufzouf.model.types.MeasurementType;
import be.pixxis.zoufzouf.persistence.MongoBean;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.rmi.server.ExportException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

/**
 * A log slurper thread.
 *
 * @author Gert Leenders
 */
public class LogSlurperThread<T> implements Callable<T> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

  private static ThreadLocal<BlobStore> blobStoreHolder = new ThreadLocal<BlobStore>() {
    @Override
    protected BlobStore initialValue() {
      return LogSlurper.blobStoreContext.getBlobStore();
    }
  };

  private static final Logger LOG = LoggerFactory.getLogger(LogSlurperThread.class);

  private long processedLines = 0;
  private LogSlurper slurper;
  private String key;

  public LogSlurperThread(final LogSlurper slurper, final String key) {
    this.slurper = slurper;
    this.key = key;
  }


  @Override
  /**
   * Process the individual log files, one by one in parallel threads
   */
  public T call() throws Exception {

    try {

      LOG.trace(" >> processing {}", key);

      // Process the log file from withing the processing folder.

      final Blob blob = blobStoreHolder.get().getBlob(slurper.getConfig().getS3().getBucket() ,
          key);
      if (blob != null) {

        final InputStream log = blob.getPayload().openStream();
        final GZIPInputStream gzipInputStream = new GZIPInputStream(log);

        try {

          final Reader decoder = new InputStreamReader(gzipInputStream, "UTF-8");
          try ( final BufferedReader reader = new BufferedReader(decoder)) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
              processRawData(line);
            }
          }

        } catch (ExportException ex) {
          ex.printStackTrace();
        }

        if (!this.slurper.getConfig().isDryRun()) {
          blobStoreHolder.get().removeBlob(slurper.getConfig().getS3().getBucket(), key);
        }

        slurper.increaseProcessedLines(processedLines);

        LOG.trace(" >> processed {}", key);

      } else {
        LOG.info("Processing failed key: '{}' not found.", key);

        try {
          if (!this.slurper.getConfig().isDryRun()) {
            blobStoreHolder.get().removeBlob(slurper.getConfig().getS3().getBucket(), key);
          }
        } catch (Exception ex) {
          LOG.error("Error while trying to delete key '{}'.", key);
        }
      }

    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
    }
    return null;
  }


  /**
   * Processing of the raw log data of a log line.
   *
   * @param line a single log line
   */
  private void processRawData(final String line) throws Exception {

    if (line.startsWith("#")) {
      // Ignore comment lines
      return;
    }

    final String[] rawLogData =
        Iterables.toArray(Splitter.on('\t').trimResults().split(line), String.class);

    // A log line should consist out of 17 or 18 fields
    if (rawLogData.length > 17 && rawLogData.length < 24) {

      // Parse line into map structure.
      final Map<String, String> logMsg = new HashMap<>();
      logMsg.put("date", rawLogData[0]);
      logMsg.put("x-edge-location", rawLogData[2]);
      logMsg.put("sc-bytes", rawLogData[3]);
      logMsg.put("cs-method", rawLogData[5]);
      logMsg.put("cs-uri-stem", rawLogData[7]);
      logMsg.put("sc-status", rawLogData[8]);
      logMsg.put("cs-uri-query", rawLogData[11]);
      logMsg.put("x-edge-result-type", rawLogData[13]);

      if (logMsg.get("cs-method").equals("GET") && logMsg.get("sc-status") != null
          && logMsg.get("sc-status").startsWith("20")) {

        processLogLine(logMsg.get("cs-uri-stem"), logMsg.get("cs-uri-query"), logMsg.get("date"),
            logMsg.get("x-edge-location"), Long.parseLong(logMsg.get("sc-bytes")));

      } else if (logMsg.get("sc-status").equals("000")) {
        // Ignore: connection closed before request was complete.
      } else if (logMsg.get("x-edge-result-type").equalsIgnoreCase("error")) {
        // Silently ignore?
      } else {
        LOG.trace(line);
      }

    } else if (rawLogData.length == 17) {

      // Pare line into map structure.
      final Map<String, String> logMsg = new HashMap<>();
      logMsg.put("date", rawLogData[0]);
      logMsg.put("x-edge-location", rawLogData[2]);
      logMsg.put("x-event", rawLogData[4]);
      logMsg.put("sc-bytes", rawLogData[5]);
      logMsg.put("x-cf-status", rawLogData[6]);
      logMsg.put("x-sname", rawLogData[13]);
      logMsg.put("x-sname-query", rawLogData[14]);

      if (logMsg.get("x-cf-status").equals("OK") && (logMsg.get("x-event").equals("play")
          || logMsg.get("x-event").equals("pause") || logMsg.get("x-event").equals("stop"))) {

        processLogLine(logMsg.get("x-sname"), logMsg.get("x-sname-query"), logMsg.get("date"),
            logMsg.get("x-edge-location"), Long.parseLong(logMsg.get("sc-bytes")));

      } else {
        LOG.trace(line);
      }

    } else {
      LOG.error("Line could not be parsed: {}.", line);
      LOG.error("--> Key: {}.", key);
    }
  }

  /**
   * Process a single log line.
   *
   * @param uriStem      an uri stem
   * @param uriQuery     an uri query
   * @param dateString   a date
   * @param edgeLocation an edge location
   * @param bytes        a number of bytes
   * @throws Exception arrer while processing a log line
   */
  private void processLogLine(final String uriStem, final String uriQuery, final String dateString,
                              final String edgeLocation, final long bytes) throws Exception {

    if (!checkUriStemExtension(uriStem)) {
      return;
    }

    String userId = null;

    final int uidIndex = uriQuery.indexOf("uid=");
    if (uidIndex > -1) {
      userId = uriQuery.substring(4, uidIndex + 4 + 23);
    }

    try {

      final Date date = FORMATTER.parseDateTime(dateString).toDate();
      final String location = EdgeLocation.getPricingRegion(edgeLocation).toString();

      if (userId != null) {
        // Add a user measurement to MongoDB
        MongoBean.INSTANCE
            .addMeasurement(MeasurementType.USER, date, userId, location, bytes, userId);
      }

      // Presentation log line
      if (uriStem.startsWith("/p/") || uriStem.startsWith("p/")) {

        final String presentationId;
        if (uriStem.startsWith("/p/")) {
          presentationId = uriStem.substring(3, 26);
        } else {
          presentationId = uriStem.substring(2, 25);
        }

        // Add a presentation measurement to MongoDB
        // MongoBean.INSTANCE.
        // addMeasurement(MeasurementType.PRESENTATION, date, presentationId, location,
        // bytes,
        // userId)

        // @GuardedBy("putIfAbsent atomic update on cache")
        String channelId = slurper.getLocalCache().get(presentationId);
        if (channelId == null) {
          // channelId = MongoBean.INSTANCE.findPresentationChannelId(presentationId)
          // LogSlurper.localCache.putIfAbsent(presentationId, channelId)
        }

        // Add a channel measurement to MongoDB
        // MongoBean.INSTANCE.addMeasurement(MeasurementType.CHANNEL, date, channelId, location,
        // bytes, userId)

      } else if (uriStem.startsWith("/co/") || uriStem.startsWith("co/")) {

        final String courseId;
        if (uriStem.startsWith("/co/")) {
          courseId = uriStem.substring(4, 27);
        } else {
          courseId = uriStem.substring(3, 26);
        }

        // Add a course measurement to MongoDB
        // MongoBean.INSTANCE.addMeasurement(MeasurementType.COURSE, date, courseId, location,
        // bytes, userId)

        // @GuardedBy("putIfAbsent atomic update on cache")
        // String channelId = LogSlurper.LOCAL_CACHE.get(courseId);
        //                if (channelId == null) {
        //                    channelId = MongoBean.INSTANCE.findCourseChannelId(courseId)
        //                    Analyzer.localCache.putIfAbsent(courseId, channelId)
        //                }

        // Add a channel measurement to MongoDB
        //  MongoBean.INSTANCE.addMeasurement(MeasurementType.CHANNEL, date, channelId, location,
        // bytes, userId)

      } else if (uriStem.startsWith("/d/") || uriStem.startsWith("d/")) {

        final String documentId;
        if (uriStem.startsWith("/d/")) {
          documentId = uriStem.substring(3, 26);
        } else {
          documentId = uriStem.substring(2, 25);
        }

        // Add a document measurement to MongoDB
        //  MongoBean.INSTANCE.addMeasurement(MeasurementType.DOCUMENT, date, documentId,
        // location, bytes,
        // userId)

        // @GuardedBy("putIfAbsent atomic update on cache")
        //                String channelId = Analyzer.localCache.get(documentId);
        //                if (channelId == null) {
        //                    channelId = MongoBean.INSTANCE.findDocumentChannelId(documentId)
        //                    Analyzer.localCache.putIfAbsent(documentId, channelId)
        //                }

        // Add a channel measurement to MongoDB
        // MongoBean.INSTANCE.addMeasurement(MeasurementType.CHANNEL, date, channelId, location,
        // bytes, userId)

      } else if (uriStem.startsWith("/c/") || uriStem.startsWith("c/")) {

        final String channelId;
        if (uriStem.startsWith("/c/")) {
          channelId = uriStem.substring(3, 26);
        } else {
          channelId = uriStem.substring(2, 25);
        }

        // Add a channel measurement to MongoDB
        // MongoBean.INSTANCE.addMeasurement(MeasurementType.CHANNEL, date, channelId, location,
        // bytes, userId)

      } else if (uriStem.startsWith("/u/") || uriStem.startsWith("u/")) {

        // User related content can be ignored.

      } else if (uriStem.startsWith("/ca/") || uriStem.startsWith("ca/")) {

        // Category related content can be ignored.

      } else if (uriStem.startsWith("/payouts/") || uriStem.startsWith("payouts/")) {

        // Payout related content can be ignored.

      } else {

        LOG.error("Add logic to process line?: {}.", uriStem);
      }

    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    } catch (PricingRegionNotFound pricingRegionNotFound) {
      pricingRegionNotFound.printStackTrace();
    }

    processedLines++;
  }


  /**
   * Check the uri stem's file extension to see if processing is needed.
   *
   * @param uriStem the uri stem
   * @return true if processing is needed
   */
  @VisibleForTesting
  boolean checkUriStemExtension(final String uriStem) {

    if (uriStem.endsWith("mp4") || uriStem.endsWith("MP4")
        || uriStem.endsWith("mp3") || uriStem.endsWith("MP3")
        || uriStem.endsWith("flv") || uriStem.endsWith("FLV")
        || uriStem.endsWith("ogg") || uriStem.endsWith("OGG")
        || uriStem.endsWith("swf") || uriStem.endsWith("SWF")
        || uriStem.endsWith("mov") || uriStem.endsWith("MOV")
        || uriStem.endsWith("png") || uriStem.endsWith("PNG")
        || uriStem.endsWith("jpg") || uriStem.endsWith("JPG")
        || uriStem.endsWith("gif") || uriStem.endsWith("GIF")
        || uriStem.endsWith("pdf") || uriStem.endsWith("PDF")
        || uriStem.endsWith("pptx") || uriStem.endsWith("PPTX")
        || uriStem.endsWith("svg") || uriStem.endsWith("SVG")
        || uriStem.endsWith("key") || uriStem.endsWith("KEY")
        || uriStem.endsWith("zip") || uriStem.endsWith("ZIP")
        || uriStem.endsWith("docx") || uriStem.endsWith("DOCX")
        || uriStem.endsWith("txt") || uriStem.endsWith("TXT")
        || uriStem.endsWith("eps") || uriStem.endsWith("EPS")
        || uriStem.endsWith("psd") || uriStem.endsWith("PSD")
        || uriStem.endsWith("m4v") || uriStem.endsWith("M4V")
        || uriStem.endsWith("jpeg") || uriStem.endsWith("JPEG")) {

      return true;

    } else if (uriStem.indexOf(".") == -1) {

      // Not an extension, process file.
      return true;

    } else if (uriStem.endsWith("xml")) {

      // Its safe to ignore these files.
      return false;

    } else {

      LOG.error("Extension to be ignored or measured: {}.", uriStem);
      return false;
    }
  }
}
