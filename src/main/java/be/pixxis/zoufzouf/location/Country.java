package be.pixxis.zoufzouf.location;

/**
 * Country enumerations.
 *
 * @author Gert Leenders
 */
public enum Country {

  THE_NETHERLANDS("The Netherlands"),
  SWEDEN("Sweden"),
  INDIA("India"),
  FRANCE("France"),
  IRELAND("Ireland"),
  GERMANY("Germany"),
  BRAZIL("Brazil"),
  HONG_KONG("Hong Kong"),
  SOUTH_COREA("South Corea"),
  UNITED_KINGDOM("United Kingdom"),
  SPAIN("Spain"),
  PHILIPPINES("Philippines"),
  ITALY("Italy"),
  JAPAN("Japan"),
  REPUBLIC_OF_SIGNAPORE("Republic of Singapore"),
  TAIWAN("Taiwan"),
  POLAND("Poland");

  private final String name;

  Country(final String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
