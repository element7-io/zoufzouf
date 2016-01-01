package be.pixxis.zoufzouf.location;

/**
 * Continent enumerations
 *
 * @author Gert Leenders
 */
public enum Continent {

    UNITED_STATES("United States"),
    EUROPE("Europe"),
    ASIA("Asia"),
    AUSTRALIA("Australia"),
    SOUTH_AMERICA("South America");

    private final String name;

    private Continent(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
