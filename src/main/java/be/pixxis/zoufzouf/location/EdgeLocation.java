package be.pixxis.zoufzouf.location;

import static be.pixxis.zoufzouf.location.City.AMSTERDAM;
import static be.pixxis.zoufzouf.location.City.ASHBURN;
import static be.pixxis.zoufzouf.location.City.ATLANTA;
import static be.pixxis.zoufzouf.location.City.CHENNAI;
import static be.pixxis.zoufzouf.location.City.DALLAS;
import static be.pixxis.zoufzouf.location.City.DUBLIN;
import static be.pixxis.zoufzouf.location.City.FRANKFURT;
import static be.pixxis.zoufzouf.location.City.HONG_KONG_ISLAND;
import static be.pixxis.zoufzouf.location.City.JACKSONVILLE;
import static be.pixxis.zoufzouf.location.City.LONDON;
import static be.pixxis.zoufzouf.location.City.LOS_ANGELES;
import static be.pixxis.zoufzouf.location.City.MADRID;
import static be.pixxis.zoufzouf.location.City.MANILA;
import static be.pixxis.zoufzouf.location.City.MARSEILLE;
import static be.pixxis.zoufzouf.location.City.MELBOURNE;
import static be.pixxis.zoufzouf.location.City.MIAMI;
import static be.pixxis.zoufzouf.location.City.MILAN;
import static be.pixxis.zoufzouf.location.City.MUMBAI;
import static be.pixxis.zoufzouf.location.City.NEWARK;
import static be.pixxis.zoufzouf.location.City.NUEVA_YORK;
import static be.pixxis.zoufzouf.location.City.PARIS;
import static be.pixxis.zoufzouf.location.City.RIO_DE_JANERIO;
import static be.pixxis.zoufzouf.location.City.SAN_FRANCISCO;
import static be.pixxis.zoufzouf.location.City.SAU_PAULO;
import static be.pixxis.zoufzouf.location.City.SEATTLE;
import static be.pixxis.zoufzouf.location.City.SEATTLE_ONE_BOX;
import static be.pixxis.zoufzouf.location.City.SEOUL;
import static be.pixxis.zoufzouf.location.City.SOUTH_BEND;
import static be.pixxis.zoufzouf.location.City.STOCKHOLM;
import static be.pixxis.zoufzouf.location.City.ST_LOUIS;
import static be.pixxis.zoufzouf.location.City.SYDNEY;
import static be.pixxis.zoufzouf.location.City.TAIPEI;
import static be.pixxis.zoufzouf.location.City.TOKYO;
import static be.pixxis.zoufzouf.location.City.WARSAW;
import static be.pixxis.zoufzouf.location.Continent.ASIA;
import static be.pixxis.zoufzouf.location.Continent.AUSTRALIA;
import static be.pixxis.zoufzouf.location.Continent.EUROPE;
import static be.pixxis.zoufzouf.location.Continent.SOUTH_AMERICA;
import static be.pixxis.zoufzouf.location.Continent.UNITED_STATES;
import static be.pixxis.zoufzouf.location.Country.BRAZIL;
import static be.pixxis.zoufzouf.location.Country.FRANCE;
import static be.pixxis.zoufzouf.location.Country.GERMANY;
import static be.pixxis.zoufzouf.location.Country.HONG_KONG;
import static be.pixxis.zoufzouf.location.Country.INDIA;
import static be.pixxis.zoufzouf.location.Country.IRELAND;
import static be.pixxis.zoufzouf.location.Country.ITALY;
import static be.pixxis.zoufzouf.location.Country.JAPAN;
import static be.pixxis.zoufzouf.location.Country.PHILIPPINES;
import static be.pixxis.zoufzouf.location.Country.POLAND;
import static be.pixxis.zoufzouf.location.Country.REPUBLIC_OF_SIGNAPORE;
import static be.pixxis.zoufzouf.location.Country.SOUTH_COREA;
import static be.pixxis.zoufzouf.location.Country.SPAIN;
import static be.pixxis.zoufzouf.location.Country.SWEDEN;
import static be.pixxis.zoufzouf.location.Country.TAIWAN;
import static be.pixxis.zoufzouf.location.Country.THE_NETHERLANDS;
import static be.pixxis.zoufzouf.location.Country.UNITED_KINGDOM;
import static be.pixxis.zoufzouf.location.PricingRegion.PR_AUSTRALIA;
import static be.pixxis.zoufzouf.location.PricingRegion.PR_EUROPE;
import static be.pixxis.zoufzouf.location.PricingRegion.PR_HONG_KONG_EO;
import static be.pixxis.zoufzouf.location.PricingRegion.PR_INDIA;
import static be.pixxis.zoufzouf.location.PricingRegion.PR_JAPAN;
import static be.pixxis.zoufzouf.location.PricingRegion.PR_SOUTH_AMERICA;
import static be.pixxis.zoufzouf.location.PricingRegion.PR_UNITED_STATES;
import static be.pixxis.zoufzouf.location.State.CALIFORNIA;
import static be.pixxis.zoufzouf.location.State.FLORIDA;
import static be.pixxis.zoufzouf.location.State.GEORGIA;
import static be.pixxis.zoufzouf.location.State.INDIANA;
import static be.pixxis.zoufzouf.location.State.MISSOURI;
import static be.pixxis.zoufzouf.location.State.NEW_JERSEY;
import static be.pixxis.zoufzouf.location.State.NEW_YORK;
import static be.pixxis.zoufzouf.location.State.TEXAS;
import static be.pixxis.zoufzouf.location.State.VIRGINIA;
import static be.pixxis.zoufzouf.location.State.WASHINGTON;

/**
 * Complete list of CloudFront Edge locations.
 *
 * @author Gert Leenders
 */
public enum EdgeLocation {

  AMS1(AMSTERDAM, THE_NETHERLANDS, null, EUROPE, PR_EUROPE),
  AMS50(AMSTERDAM, THE_NETHERLANDS, null, EUROPE, PR_EUROPE),
  ARN1(STOCKHOLM, SWEDEN, null, EUROPE, PR_EUROPE),
  ATL50(ATLANTA, null, GEORGIA, UNITED_STATES, PR_UNITED_STATES),
  BOM2(MUMBAI, INDIA, null, ASIA, PR_INDIA),
  CDG3(PARIS, FRANCE, null, EUROPE, PR_EUROPE),
  CDG50(PARIS, FRANCE, null, EUROPE, PR_EUROPE),
  CDG51(PARIS, FRANCE, null, EUROPE, PR_EUROPE),
  DFW3(DALLAS, null, TEXAS, UNITED_STATES, PR_UNITED_STATES),
  DFW50(DALLAS, null, TEXAS, UNITED_STATES, PR_UNITED_STATES),
  DUB2(DUBLIN, IRELAND, null, EUROPE, PR_EUROPE),
  EWR2(NEWARK, null, NEW_JERSEY, UNITED_STATES, PR_UNITED_STATES),
  FRA2(FRANKFURT, GERMANY, null, EUROPE, PR_EUROPE),
  FRA50(FRANKFURT, GERMANY, null, EUROPE, PR_EUROPE),
  FRA6(FRANKFURT, GERMANY, null, EUROPE, PR_EUROPE),
  GRU1(SAU_PAULO, BRAZIL, null, SOUTH_AMERICA, PR_SOUTH_AMERICA),
  GIG50(RIO_DE_JANERIO, BRAZIL, null, SOUTH_AMERICA, PR_SOUTH_AMERICA),
  HKG1(HONG_KONG_ISLAND, HONG_KONG, null, ASIA, PR_HONG_KONG_EO),
  HKG50(HONG_KONG_ISLAND, HONG_KONG, null, ASIA, PR_HONG_KONG_EO),
  HKG51(HONG_KONG_ISLAND, HONG_KONG, null, ASIA, PR_HONG_KONG_EO),
  IAD12(ASHBURN, null, VIRGINIA, UNITED_STATES, PR_UNITED_STATES),
  IAD2(ASHBURN, null, VIRGINIA, UNITED_STATES, PR_UNITED_STATES),
  IAD53(ASHBURN, null, VIRGINIA, UNITED_STATES, PR_UNITED_STATES),
  ICN50(SEOUL, SOUTH_COREA, null, ASIA, PR_HONG_KONG_EO),
  ICN51(SEOUL, SOUTH_COREA, null, ASIA, PR_HONG_KONG_EO),
  IND6(SOUTH_BEND, null, INDIANA, UNITED_STATES, PR_UNITED_STATES),
  JAX1(JACKSONVILLE, null, FLORIDA, UNITED_STATES, PR_UNITED_STATES),
  JFK1(NUEVA_YORK, null, NEW_YORK, UNITED_STATES, PR_UNITED_STATES),
  JFK5(NUEVA_YORK, null, NEW_YORK, UNITED_STATES, PR_UNITED_STATES),
  JFK6(NUEVA_YORK, null, NEW_YORK, UNITED_STATES, PR_UNITED_STATES),
  LAX1(LOS_ANGELES, null, CALIFORNIA, UNITED_STATES, PR_UNITED_STATES),
  LAX3(LOS_ANGELES, null, CALIFORNIA, UNITED_STATES, PR_UNITED_STATES),
  LHR3(LONDON, UNITED_KINGDOM, null, EUROPE, PR_EUROPE),
  LHR5(LONDON, UNITED_KINGDOM, null, EUROPE, PR_EUROPE),
  LHR50(LONDON, UNITED_KINGDOM, null, EUROPE, PR_EUROPE),
  MAA3(CHENNAI, INDIA, null, ASIA, PR_INDIA),
  MAD50(MADRID, SPAIN, null, EUROPE, PR_EUROPE),
  MEL50(MELBOURNE, null, null, AUSTRALIA, PR_AUSTRALIA),
  MIA3(MIAMI, null, FLORIDA, UNITED_STATES, PR_UNITED_STATES),
  MIA50(MIAMI, null, FLORIDA, UNITED_STATES, PR_UNITED_STATES),
  MNL50(MANILA, PHILIPPINES, null, ASIA, PR_HONG_KONG_EO),
  MRS50(MARSEILLE, FRANCE, null, EUROPE, PR_EUROPE),
  MXP4(MILAN, ITALY, null, EUROPE, PR_EUROPE),
  NRT12(TOKYO, JAPAN, null, ASIA, PR_JAPAN),
  NRT52(TOKYO, JAPAN, null, ASIA, PR_JAPAN),
  NRT53(TOKYO, JAPAN, null, ASIA, PR_JAPAN),
  NRT54(TOKYO, JAPAN, null, ASIA, PR_JAPAN),
  SEA4(SEATTLE, null, WASHINGTON, UNITED_STATES, PR_UNITED_STATES),
  SEA50(SEATTLE, null, WASHINGTON, UNITED_STATES, PR_UNITED_STATES),
  SEA50_ONE_BOX(SEATTLE_ONE_BOX, null, WASHINGTON, UNITED_STATES, PR_UNITED_STATES),
  SFO4(SAN_FRANCISCO, null, CALIFORNIA, UNITED_STATES, PR_UNITED_STATES),
  SFO5(SAN_FRANCISCO, null, CALIFORNIA, UNITED_STATES, PR_UNITED_STATES),
  SFO9(SAN_FRANCISCO, null, CALIFORNIA, UNITED_STATES, PR_UNITED_STATES),
  SFO20(SAN_FRANCISCO, null, CALIFORNIA, UNITED_STATES, PR_UNITED_STATES),
  SIN2(null, REPUBLIC_OF_SIGNAPORE, null, ASIA, PR_HONG_KONG_EO),
  SIN3(null, REPUBLIC_OF_SIGNAPORE, null, ASIA, PR_HONG_KONG_EO),
  STL2(ST_LOUIS, null, MISSOURI, UNITED_STATES, PR_UNITED_STATES),
  SYD1(SYDNEY, null, null, AUSTRALIA, PR_AUSTRALIA),
  TPE50(TAIPEI, TAIWAN, null, ASIA, PR_HONG_KONG_EO),
  WAW50(WARSAW, POLAND, null, EUROPE, PR_EUROPE);

  private final City city;
  private final Country country;
  private final State state;
  private final Continent continent;
  private final PricingRegion pricingRegion;

  EdgeLocation(final City city, final Country country, final State state, final Continent continent,
               final PricingRegion pricingRegion) {

    this.city = city;
    this.country = country;
    this.state = state;
    this.continent = continent;
    this.pricingRegion = pricingRegion;
  }

  /**
   * Retrieve the pricing region from a given edge location.
   *
   * @param edgeLocation an edge location
   * @return a pricing region
   * @throws PricingRegionNotFound Exception if no pricing region was found for the edge location
   */
  public static PricingRegion getPricingRegion(final String edgeLocation)
      throws PricingRegionNotFound {

    final EdgeLocation[] values = EdgeLocation.values();
    for (EdgeLocation location : values) {
      if (location.name().equals(edgeLocation)) {
        return location.pricingRegion;
      }
    }
    throw new PricingRegionNotFound("Price region could be found for location: " + edgeLocation);
  }

  @Override
  public String toString() {
    String value = this.name() + " | "
        + this.city + " | "
        + (this.country == null ? "-" : this.country) + " | "
        + (this.state == null ? "-" : this.state) + " | "
        + this.continent + " | "
        + this.pricingRegion;
    return value;
  }
}
