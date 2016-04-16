package be.pixxis.zoufzouf.location;

/**
 * City enumeration
 *
 * @author Gert Leenders
 */
public enum City {

    AMSTERDAM("Amsterdam"),
    STOCKHOLM("Stockholm"),
    ATLANTA("Atlanta"),
    MUMBAI("Mumbai"),
    PARIS("Paris"),
    DALLAS("Dallas"),
    DUBLIN("Dublin"),
    NEWARK("Newark"),
    FRANKFURT("Frankfurt"),
    SAU_PAULO("Sau Paulo"),
    RIO_DE_JANERIO("Rio de Janerio"),
    HONG_KONG_ISLAND("Hong Kong Island"),
    ASHBURN("Ashburn"),
    SEOUL("Seoul"),
    SOUTH_BEND("South Bend"),
    JACKSONVILLE("Jacksonville"),
    NUEVA_YORK("Nueva York"),
    LOS_ANGELES("Los Angeles"),
    LONDON("London"),
    CHENNAI("Chennai"),
    MADRID("Madrid"),
    MELBOURNE("Melbourne"),
    MIAMI("Miami"),
    MANILA("Manila"),
    MARSEILLE("Marseille"),
    MILAN("Milan"),
    TOKYO("Tokyo"),
    SEATTLE("Seattle"),
    SEATTLE_ONE_BOX("SEA50-OneBox"),
    SAN_FRANCISCO("San Francisco"),
    ST_LOUIS("St. Louis"),
    SYDNEY("Sydney"),
    TAIPEI("Taipei"),
    WARSAW("Warsaw");

    private final String name;

    City(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
