package be.pixxis.zoufzouf.persistence;

import be.pixxis.zoufzouf.Configuration;
import be.pixxis.zoufzouf.model.types.MeasurementType;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import net.jcip.annotations.GuardedBy;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Singleton for interaction with MongoDB.
 *
 * @author Gert Leenders
 */
public enum MongoBean {

  INSTANCE;

  private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("dd-MM-yyyy");
  private static final String CLOUDFRONT = "cloudfront";

  private static final Logger LOG = LoggerFactory.getLogger(MongoBean.class);
  private MongoClient mongo = null;


  /**
   * Setup a new MongoDB connection.
   */
  public void init(final List<Configuration.ServerAddress> servers) {

    final List<ServerAddress> addresses = new ArrayList<>();
    servers.stream().forEach(server -> {
      addresses.add(new ServerAddress(server.getHost(), server.getPort()));
    });


    final ReadPreference readPreference = ReadPreference.primaryPreferred();
    final WriteConcern writeConcern = WriteConcern.ACKNOWLEDGED;

    final MongoClientOptions mongoOptions = new MongoClientOptions.Builder()
        .connectTimeout(15000)
        .connectionsPerHost(100)
        .cursorFinalizerEnabled(true)
        .socketKeepAlive(true)
        .maxWaitTime(15000)
        .socketTimeout(15000)
        .threadsAllowedToBlockForConnectionMultiplier(5)
        .readPreference(readPreference)
        .writeConcern(writeConcern)
        .build();

    if (addresses.size() == 1) {
      mongo = new MongoClient(addresses.get(0), mongoOptions);
    } else {
      mongo = new MongoClient(addresses, mongoOptions);
    }

    mongo.setReadPreference(readPreference);
    mongo.setWriteConcern(writeConcern);

    //                                LOG.debug("Connecting to MongoDB on localhost (DEV).")
  }

  /**
   * Shutdown the MongoDB connection.
   */
  public void shutdown() {
    //        if (connectionParams == null) {
    //            LOG.debug("Shutting down MongoDB connection on localhost")
    //        } else {
    //            LOG.debug("Shutting down MongoDB ReplSe connectiont: {}", connectionParams)
    //        }
    mongo.close();
  }

  private MongoDatabase getDatabase() {

    if (mongo == null) {
      throw new RuntimeException("Mongo DB should have been initialized");
    }

    final MongoDatabase cloudfront = mongo.getDatabase(CLOUDFRONT);
    return cloudfront;
  }

  /**
   * Add a new  bandwidth measurement record if none exists, otherwise it just increments the number
   * of bytes.
   *
   * @param type  PRESENTATION, CHANNEL or USER
   * @param date  date of the measurement
   * @param id    id of the object
   * @param bytes number of bytes measured
   */
  @GuardedBy("MongoDB atomic updates")
  public void addMeasurement(final MeasurementType type, final Date date, final String id,
                             final String location, final long bytes, final String userId) {


    final UpdateOptions options = new UpdateOptions().upsert(true);

    // Measurement by day
    final Bson dayFilter = new Document("type", type.toString())
        .append("date", date)
        .append("id", id)
        .append("pricingRegion", location);

    Bson update = new Document("$inc", new Document("bytes", bytes).append("requests", 1))
        .append("$set", new Document("needsProcessing", true));

    getDailyBandwidthUsage().updateOne(dayFilter, update, options);


    // Measurement by month
    final DateTime dateTime = new DateTime(date);

    final Bson monthFilter = new Document("type", type.toString())
        .append("month", dateTime.getMonthOfYear())
        .append("year", dateTime.getYear())
        .append("id", id)
        .append("pricingRegion", location);

    update = new Document("$inc", new Document("bytes", bytes).append("requests", 1))
        .append("$set", new Document("needsProcessing", true)
            .append("date", new DateTime(date).withDayOfMonth(1).toDate()));

    getMonthlyBandwidthUsage().updateOne(monthFilter, update, options);

    if (userId != null && type != MeasurementType.USER) {
      addUserMeasurement(type, date, id, location, bytes, dateTime, userId);
    }
  }

  @GuardedBy("this")
  private synchronized void addUserMeasurement(final MeasurementType type, final Date date,
                                               final String id, final String location,
                                               final long bytes, final DateTime dateTime,
                                               final String userId) {

    // Measurement by day

    // Add the user to the nested array if it not yet exists.
    Bson dayFilter = new Document("type", type.toString())
        .append("date", date)
        .append("id", new ObjectId(id))
        .append("pricingRegion", location)
        .append("users.id", new Document("$ne", new ObjectId(userId)));

    Bson update = new Document("$push", new Document("users",
        new Document("id", new ObjectId(userId)).append("bytes", 0).append("requests", 0)));

    getDailyBandwidthUsage().updateOne(dayFilter, update);

    // Add the number of bytes to the user's data
    dayFilter = new Document("type", type.toString())
        .append("date", date)
        .append("id", new ObjectId(id))
        .append("pricingRegion", location)
        .append("users.id", new ObjectId(userId));

    update = new Document("$inc", new Document("users.$.bytes", bytes)
        .append("users.$.requests", 1));

    getDailyBandwidthUsage().updateOne(dayFilter, update);


    // Measurement by month

    // Add the user to the nested array if it not yet exists.
    Bson monthFilter = new Document("type", type.toString())
        .append("month", dateTime.getMonthOfYear())
        .append("year", dateTime.getYear())
        .append("id", new ObjectId(id))
        .append("pricingRegion", location)
        .append("users.id", new Document("$ne", new ObjectId(userId)));

    update = new Document("$push", new Document("users", new Document("id", new ObjectId(userId))
        .append("bytes", 0).append("requests", 0)));

    getMonthlyBandwidthUsage().updateOne(monthFilter, update);

    // Add the number of bytes to the user's data
    monthFilter = new BasicDBObject("type", type.toString())
        .append("month", dateTime.getMonthOfYear())
        .append("year", dateTime.getYear())
        .append("id", new ObjectId(id))
        .append("pricingRegion", location)
        .append("users.id", new ObjectId(userId));

    update = new Document("$inc",
        new Document("users.$.bytes", bytes).append("users.$.requests", 1));

    getMonthlyBandwidthUsage().updateOne(monthFilter, update);
  }

  /**
   * MongoDb collection to store the measurement data.
   * The usage of an upsert requires a unique index to avoid duplicates.
   * db.bandwidthusage
   * .ensureIndex( { type: 1, date: 1, id: 1, pricingRegion: 1 }, { unique: true } )
   *
   * @return the bandwidthusage collection
   */
  private MongoCollection<Document> getDailyBandwidthUsage() {
    return getDatabase().getCollection("dailybandwidthusage");
  }

  /**
   * MongoDb collection to store the measurement data.
   * The usage of an upsert requires a unique index to avoid duplicates.
   * db.bandwidthusage
   * .ensureIndex( { type: 1, date: 1, id: 1, pricingRegion: 1 }, { unique: true } )
   *
   * @return the bandwidthusage collection
   */
  private MongoCollection<Document> getMonthlyBandwidthUsage() {
    return getDatabase().getCollection("monthlybandwidthusage");
  }
}