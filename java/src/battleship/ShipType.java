package battleship;

public enum ShipType {
    AIRCRAFT_CARRIER("Aircraft Carrier", 'A',  5),
    BATTLESHIP("Battleship", 'B', 4),
    SUBMARINE("Submarine", 'S', 3),
    CRUISER("Cruiser", 'C', 3),
    DESTROYER("Destroyer", 'D', 2);

    private final String name;

    private final Character identifier;
    private final int size;

    ShipType(String name, Character identifier, int size) {
        this.name = name;
        this.identifier = identifier;
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public Character getIdentifier() {
        return identifier;
    }

    public int getSize() {
        return this.size;
    }
}
