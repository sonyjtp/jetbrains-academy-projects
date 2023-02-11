package battleship;

import java.util.List;

public record Ship(ShipType shipType, List<String> coordinates) {
}
