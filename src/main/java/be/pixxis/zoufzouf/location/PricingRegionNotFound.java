package be.pixxis.zoufzouf.location;

/**
 * Exception returned in case a pricing region could not be found for an edge location.
 *
 * @author Gert Leenders
 */
public class PricingRegionNotFound extends Throwable {

  public PricingRegionNotFound(final String msg) {
    super(msg);
  }
}