package be.pixxis.zoufzouf;

import be.pixxis.zoufzouf.model.types.Storage;

import java.util.List;

/**
 * @author Gert Leenders
 *
 * Configuration reflecting yaml configuration
 */
public class Configuration {
    private String awsAccessKey;
    private String awsSecretKey;
    private boolean dryRun;
    private Storage storage;
    private List<String> servers;

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(final Storage storage) {
        this.storage = storage;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(final List<String> servers) {
        this.servers = servers;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(final String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public void setAwsAccessKey(final String awsAccessKey) {
        this.awsAccessKey = awsAccessKey;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }
}
