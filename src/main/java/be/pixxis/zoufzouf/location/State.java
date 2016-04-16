package be.pixxis.zoufzouf.location;

/**
 * State enumeration
 *
 * @author Gert Leenders
 */
public enum State {

    INDIANA("Indiana"),
    FLORIDA("Florida"),
    NEW_YORK("New York"),
    CALIFORNIA("California"),
    NEW_JERSEY("New Jersey"),
    MISSOURI("Missouri"),
    GEORGIA("Georgia"),
    TEXAS("Texas"),
    VIRGINIA("Virginia"),
    WASHINGTON("Washington");

    private final String name;

    State(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
