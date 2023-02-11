package battleship;

import java.util.List;
import java.util.Map;

public record Player(String name, Ocean ocean, Map<String, Character> occupiedMap, List<Character> occupiedList) {
}
