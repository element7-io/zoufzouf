package be.pixxis.zoufzouf.location;

/**
 * Pricing region enumerations.
 *
 * @author Gert Leenders
 */
public enum PricingRegion {

  PR_UNITED_STATES("United States"),
  PR_EUROPE("Europe"),
  PR_HONG_KONG_EO("Hong Kong and others"),
  PR_AUSTRALIA("Australia"),
  PR_SOUTH_AMERICA("South America"),
  PR_JAPAN("Japan"),
  PR_INDIA("India");

  private final String name;

  PricingRegion(final String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
