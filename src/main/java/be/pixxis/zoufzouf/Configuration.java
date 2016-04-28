package be.pixxis.zoufzouf;

import be.pixxis.zoufzouf.model.types.ProvisioningType;
import be.pixxis.zoufzouf.model.types.StorageType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configuration reflecting yaml configuration.
 *
 * @author Gert Leenders
 */
public class Configuration {

  private static final Pattern REGEX_MONGO_URL =
      Pattern.compile("(([a-zA-Z0-9]+):([a-zA-Z0-9]+)@)?([a-zA-Z0-9\\.]+):([0-9]+),?");
  private int threads;
  private int batchSize;
  private ProvisioningType logProvisioning;
  private boolean dryRun;
  private S3 s3;
  private StorageType storage;
  private List<ServerAddress> serverAddresses;

  public ProvisioningType getLogProvisioning() {
    return logProvisioning;
  }

  public void setLogProvisioning(ProvisioningType logProvisioning) {
    this.logProvisioning = logProvisioning;
  }

  public S3 getS3() {
    return s3;
  }

  public void setS3(S3 s3) {
    this.s3 = s3;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public int getThreads() {
    return threads;
  }

  public void setThreads(final int threads) {
    this.threads = threads;
  }

  public List<ServerAddress> getServerAddresses() {
    return serverAddresses;
  }

  public void setServerAddresses(final List<ServerAddress> serverAddresses) {
    this.serverAddresses = serverAddresses;
  }

  public StorageType getStorage() {
    return storage;
  }

  public void setStorage(final StorageType storage) {
    this.storage = storage;
  }

  /**
   * Construct a list of servers address (host + port).
   *
   * @param servers a list of address in the format host:port
   */
  public void setServers(final List<String> servers) {
    this.serverAddresses = new ArrayList<>();
    servers.stream().forEach(address -> {
      final String ip = address.substring(address.lastIndexOf('/') + 1, address.lastIndexOf(':'));
      final String port = address.substring(address.lastIndexOf(':') + 1);
      this.serverAddresses.add(new ServerAddress(ip, Integer.valueOf(port)));
    });
  }

  public boolean isDryRun() {
    return dryRun;
  }

  public void setDryRun(final boolean dryRun) {
    this.dryRun = dryRun;
  }

  /**
   * Initialise the server address from a string in the format host:port.
   *
   * @param mongoUrl a server url in the format host:port
   */
  public void setFromEnv(final String mongoUrl) {

    this.serverAddresses = new ArrayList<>();

    final Matcher matcher = REGEX_MONGO_URL.matcher(mongoUrl);
    while (matcher.find()) {
      this.serverAddresses.add(new ServerAddress(matcher.group(4),
          Integer.parseInt(matcher.group(5))));
    }
  }

  public static class ServerAddress {
    private final String host;
    private final int port;


    public ServerAddress(final String host, final int port) {
      this.host = host;
      this.port = port;
    }

    public int getPort() {
      return port;
    }

    public String getHost() {
      return host;
    }
  }

  public static class S3 {
    private String awsAccessKey;
    private String awsSecretKey;
    private String bucket;
    private String logsKey;
    private String logsProcessingKey;

    public String getBucket() {
      return bucket;
    }

    public void setBucket(String bucket) {
      this.bucket = bucket;
    }

    public String getAwsSecretKey() {
      return awsSecretKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
      this.awsSecretKey = awsSecretKey;
    }

    public String getAwsAccessKey() {
      return awsAccessKey;
    }

    public void setAwsAccessKey(String awsAccessKey) {
      this.awsAccessKey = awsAccessKey;
    }

    public String getLogsKey() {
      return logsKey;
    }

    public void setLogsKey(String logsKey) {
      this.logsKey = logsKey;
    }

    public String getLogsProcessingKey() {
      return logsProcessingKey;
    }

    public void setLogsProcessingKey(String logsProcessingKey) {
      this.logsProcessingKey = logsProcessingKey;
    }
  }
}
