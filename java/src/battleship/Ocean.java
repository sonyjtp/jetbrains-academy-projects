package battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static battleship.Constants.ROW_NAMES;
import static battleship.Constants.SIZE;

public class Ocean {

    private final String[][] cells = new String[SIZE][SIZE];

    private String[][] gameCells = new String[SIZE][SIZE];
    private final List<Ship> ships = new ArrayList<>(5);


    void prepareBattleGround() {
        initCells();
        placeShips();
    }

    void draw(String[][] cellArray) {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int row = 0; row < SIZE; row ++) {
            System.out.printf("%s ", ROW_NAMES.get(row));
            for (int col = 0; col < SIZE; col ++) {
                System.out.print(cellArray[row][col]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void initCells() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for(int row = 0; row< SIZE; row ++) {
            System.out.printf("%s ", ROW_NAMES.get(row));
            for(int col = 0; col< SIZE; col ++) {
                cells[row][col] = "~ ";
                System.out.print("~ ");
            }
            System.out.println();
        }
    }

    private void placeShips(){
        place(ShipType.AIRCRAFT_CARRIER);
        place(ShipType.BATTLESHIP);
        place(ShipType.SUBMARINE);
        place(ShipType.CRUISER);
        place(ShipType.DESTROYER);
    }

    private void place(ShipType type) {
        System.out.printf("Enter the coordinates of the %s (%d cells):\n", type.getName(), type.getSize());
        boolean isValid = false;
        while (!isValid) {
            Scanner scanner = new Scanner(System.in);
            String begin  = scanner.next();
            String end = scanner.next();
            int validCoord = isValidCoordinates(type, begin, end);
            if(0 == validCoord) {
                if(!isClose(begin, end)) {
                    isValid = true;
                    ships.add(new Ship(type, markCells(begin, end)));
                    draw(cells);
                } else {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                }
            } else if (-2 == validCoord) {
                System.out.println("Error! Wrong ship location! Try again:");
            } else {
                System.out.printf("Error! Wrong length of the %s! Try again:\n", type.getName());
            }
        }
    }

    private int isValidCoordinates(ShipType type, String begin, String end) {
        Map<String, Integer> beginEnd = getBeginEnd(begin, end);
        if (!beginEnd.get("beginRow").equals(beginEnd.get("endRow"))
                && !beginEnd.get("beginCol").equals(beginEnd.get("endCol"))) {
            return -2;
        }
        if (beginEnd.get("beginRow").equals(beginEnd.get("endRow"))
                && Math.abs(beginEnd.get("beginCol") - beginEnd.get("endCol")) == type.getSize() - 1) {
            return 0;
        }
        if (beginEnd.get("beginCol").equals(beginEnd.get("endCol"))
                && Math.abs(beginEnd.get("beginRow") - beginEnd.get("endRow")) == type.getSize() - 1) {
            return 0;
        }
        return -1;
    }

    private boolean isClose(String begin, String end) {
        Map<String, Integer> beginEnd =  getBeginEnd(begin, end);
        if (beginEnd.get("beginRow").equals(beginEnd.get("endRow"))) {
            for(int i = beginEnd.get("from") - 1; i <= beginEnd.get("to") + 1; i++) {
                if (i < 0 || i >= SIZE)
                    continue;
                if (!cells[beginEnd.get("beginRow")][i].equals("~ ")) return true;
            }
        } else {
            for(int i = beginEnd.get("from") -1; i <= beginEnd.get("to") + 1; i++) {
                if (i < 0 || i >= SIZE)
                    continue;
                if (!cells[i][beginEnd.get("beginCol")].equals("~ ")) return true;
            }
        }
        return false;
    }

    private List<String> markCells(String begin, String end) {
        Map <String, Integer> beginEnd = getBeginEnd(begin, end);
        List<String> coordinates = new ArrayList<>();
        if (beginEnd.get("beginRow").equals(beginEnd.get("endRow"))) {
            for(int i = beginEnd.get("from"); i <= beginEnd.get("to"); i++) {
                cells[beginEnd.get("beginRow")][i] = "O ";
                coordinates.add(String.format("%d%d", beginEnd.get("beginRow"), i));
            }
        } else {
            for(int i = beginEnd.get("from"); i <= beginEnd.get("to"); i++) {
                cells[i][beginEnd.get("beginCol")] = "O ";
                coordinates.add(String.format("%d%d", i, beginEnd.get("beginCol")));
            }
        }
        return coordinates;
    }

    private Map<String, Integer> getBeginEnd(String begin, String end) {
        int beginRow = ROW_NAMES.indexOf(begin.charAt(0));
        int endRow = ROW_NAMES.indexOf(end.charAt(0));
        int beginCol = Integer.parseInt(begin.substring(1)) - 1;
        int endCol = Integer.parseInt(end.substring(1)) - 1;
        String minMax = beginRow ==  endRow ?
                getMinMax(beginCol, endCol) :
                getMinMax(beginRow, endRow);
        int from = Integer.parseInt(minMax.substring(0, minMax.indexOf(":")));
        int to = Integer.parseInt(minMax.substring(minMax.indexOf(":") + 1));
        return Map.of("beginRow", beginRow, "endRow", endRow,
                "beginCol", beginCol, "endCol", endCol,
                "from", from, "to", to);
    }

    private String getMinMax(int a, int b) {
        return String.format("%d:%d", Math.min(a, b), Math.max(a, b));
    }

    public String[][] getCells() {
        return cells;
    }

    public List<Ship> getShips() {
        return ships;
    }

    public String[][] getGameCells() {
        return gameCells;
    }

    public void setGameCells(String[][] gameCells) {
        this.gameCells = gameCells;
    }
}
