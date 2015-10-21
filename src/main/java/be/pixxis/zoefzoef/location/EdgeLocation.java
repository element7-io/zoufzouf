package be.pixxis.zoefzoef.location;

import java.util.concurrent.ExecutionException;


/**
 * @author Gert Leenders
 * @version $Id$
 *          <p>
 *          http://aws.amazon.com/cloudfront/details/
 *          http://aws.amazon.com/about-aws/globalinfrastructure/#reglink-na
 *          http://aws.amazon.com/cloudfront/pricing/
 *          <p>
 *          http://blog.domenech.org/2013/10/amazon-web-services-cloudfront-edgelocation-codes.html
 *          <p>
 *          http://en.wikipedia.org/wiki/International_Air_Transport_Association_airport_code
 */
public enum EdgeLocation
{
  AMS1("Amsterdam", "The Netherlands", null, "Europe", "Europe"),
  AMS50("Amsterdam", "The Netherlands", null, "Europe", "Europe"),
  ARN1("Stockholm", "Sweden", null, "Europe", "Europe"),
  ATL50("Atlanta", null, "Georgia", "United States", "United States"),
  BOM2("Mumbai", "India", null, "Asia", "India"),
  CDG3("Paris", "France", null, "Europe", "Europe"),
  CDG50("Paris", "France", null, "Europe", "Europe"),
  CDG51("Paris", "France", null, "Europe", "Europe"),
  DFW3("Dallas", null, "Texas", "United States", "United States"),
  DFW50("Dallas", null, "Texas", "United States", "United States"),
  DUB2("Dublin", "Ireland", null, "Europe", "Europe"),
  EWR2("Newark", null, "New Jersey", "United States", "United States"),
  FRA2("Frankfurt", "Germany", null, "Europe", "Europe"),
  FRA50("Frankfurt", "Germany", null, "Europe", "Europe"),
  FRA6("Frankfurt", "Germany", null, "Europe", "Europe"),
  GRU1("Sau Paulo", "Brazil", null, "South America", "South America"),
  GIG50("Rio de Janerio", "Brazil", null, "South America", "South America"),
  HKG1("Hong Kong Island", "Hong Kong", null, "Asia", "Hong Kong and others"),
  HKG50("Hong Kong Island", "Hong Kong", null, "Asia", "Hong Kong and others"),
  HKG51("Hong Kong Island", "Hong Kong", null, "Asia", "Hong Kong and others"),
  IAD12("Ashburn", null, "Virginia", "United States", "United States"),
  IAD2("Ashburn", null, "Virginia", "United States", "United States"),
  IAD53("Ashburn", null, "Virginia", "United States", "United States"),
  ICN50("Seoul", "South Corea", null, "Asia", "Hong Kong and others"),
  ICN51("Seoul", "South Corea", null, "Asia", "Hong Kong and others"),
  IND6("South Bend", null, "Indiana", "United States", "United States"),
  JAX1("Jacksonville", null, "Florida", "United States", "United States"),
  JFK1("Nueva York", null, "New York", "United States", "United States"),
  JFK5("Nueva York", null, "New York", "United States", "United States"),
  JFK6("Nueva York", null, "New York", "United States", "United States"),
  LAX1("Los Angeles", null, "California", "United States", "United States"),
  LAX3("Los Angeles", null, "California", "United States", "United States"),
  LHR3("London", "United Kingdom", null, "Europe", "Europe"),
  LHR5("London", "United Kingdom", null, "Europe", "Europe"),
  LHR50("London", "United Kingdom", null, "Europe", "Europe"),
  MAA3("Chennai", "India", null, "Asia", "India"),
  MAD50("Madrid", "Spain", null, "Europe", "Europe"),
  MEL50("Melbourne", null, null, "Australia", "Australia"),
  MIA3("Miami", null, "Florida", "United States", "United States"),
  MIA50("Miami", null, "Florida", "United States", "United States"),
  MNL50("Manila", "Philippines", null, "Asia", "Hong Kong and others"),
  MRS50("Marseille", "France", null, "Europe", "Europe"),
  MXP4("Milan", "Italy", null, "Europe", "Europe"),
  NRT12("Tokyo", "Japan", null, "Asia", "Japan"),
  NRT52("Tokyo", "Japan", null, "Asia", "Japan"),
  NRT53("Tokyo", "Japan", null, "Asia", "Japan"),
  NRT54("Tokyo", "Japan", null, "Asia", "Japan"),
  SEA4("Seattle", null, "Washington", "United States", "United States"),
  SEA50("Seattle", null, "Washington", "United States", "United States"),
  SEA50_ONE_BOX("SEA50-OneBox", "Seattle", null, "Washington", "United States", "United States"),
  SFO4("San Francisco", null, "California", "United States", "United States"),
  SFO5("San Francisco", null, "California", "United States", "United States"),
  SFO9("San Francisco", null, "California", "United States", "United States"),
  SFO20("San Francisco", null, "California", "United States", "United States"),
  SIN2(null, "Republic of Singapore", null, "Asia", "Hong Kong and others"),
  SIN3(null, "Republic of Singapore", null, "Asia", "Hong Kong and others"),
  STL2("St. Louis", null, "Missouri", "United States", "United States"),
  SYD1("Sydney", null, null, "Australia", "Australia"),
  TPE50("Taipei", "Taiwan", null, "Asia", "Hong Kong and others"),
  WAW50("Warsaw", "Poland", null, "Europe", "Europe");

  private final String city;
  private final String country;
  private final String state;
  private final String continent;
  private final String pricingRegion;
  private final String edgeLocation;

  private EdgeLocation(final String edgeLocation, final String city, final String country, final String state, final String continent,
      final String pricingRegion) throws ExecutionException
  {

    this.edgeLocation = edgeLocation;
    this.city = city;
    this.country = country;
    this.state = state;
    this.continent = continent;
    this.pricingRegion = pricingRegion;
  }

  private EdgeLocation(final String city, final String country, final String state, final String continent,
      final String pricingRegion) throws ExecutionException
  {

    this.edgeLocation = null;
    this.city = city;
    this.country = country;
    this.state = state;
    this.continent = continent;
    this.pricingRegion = pricingRegion;
  }

  static String getPricingRegion(final String edgeLocation) throws Exception
  {

    final EdgeLocation[] values = EdgeLocation.values();
    for (EdgeLocation location : values)
    {
      if (location.toString().equals(edgeLocation))
      {
        return location.pricingRegion;
      }
    }
    throw new Exception("Price region could be found for location: ${edgeLocation}");
  }

  @Override
  public String toString()
  {
    if (edgeLocation != null)
    {
      return edgeLocation;
    }
    return super.toString();
  }
}
