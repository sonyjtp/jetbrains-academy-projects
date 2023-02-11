package battleship;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static battleship.Constants.ROW_NAMES;
import static battleship.Constants.SIZE;

public class Game {

    public void play(List<Player> players) {
        int i = 0;
        boolean isGameOver = false;
        initGameCells(players.get(0));
        initGameCells(players.get(1));
        while (!isGameOver) {
            System.out.println("Press Enter and pass the move to another player");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            int j = Math.abs(1 - i);
            players.get(0).ocean().draw(players.get(j).ocean().getGameCells());
            System.out.println("---------------------");
            players.get(1).ocean().draw(players.get(i).ocean().getCells());
            System.out.printf("\n%s, it's your turn:", players.get(i).name());
            isGameOver = playNext(players.get(j));
            i = j;
        }
    }

    private void initGameCells(Player defender) {
        String[][] cells = defender.ocean().getCells();
        markOccupied(defender);
        String[][] gameCells = Arrays.stream(cells).map(String[]::clone).toArray(String[][]::new);
        markCells(gameCells);
        defender.ocean().setGameCells(gameCells);
    }

    private boolean playNext(Player defender) {
        boolean finishedRound = false;
        while (!finishedRound) {
            Scanner scanner = new Scanner(System.in);
            String shot = scanner.nextLine();
            int row = ROW_NAMES.indexOf(shot.charAt(0));
            int col = Integer.parseInt(shot.substring(1)) - 1;
            if (row == -1 || col >= SIZE) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                continue;
            }
            if (hit(defender, row, col)) {
                if (handleHit(defender, shot, row, col)) return true;
            } else {
                handleMiss(defender, row, col);
            }
            finishedRound = true;
        }
        return false;
    }

    private boolean handleHit(Player defender, String shot, int row, int col) {
        refresh(defender.ocean(), defender.ocean().getGameCells(), row, col, "X ");
        if (hasSunk(defender, shot)) {
            if (defender.occupiedList().size() == 0) {
                System.out.println("You sank the last ship. You won. Congratulations!");
                return true;
            } else {
                System.out.println("You sank a ship! Specify a new target:");
            }
        } else {
            System.out.println("You hit a ship!");
        }
        refresh(defender.ocean(), defender.ocean().getCells(), row, col, "X ");
        return false;
    }

    private void handleMiss(Player defender, int row, int col) {
        refresh(defender.ocean(), defender.ocean().getGameCells(), row, col, "M ");
        System.out.println("You missed!");
        refresh(defender.ocean(), defender.ocean().getCells(), row, col, "M ");
    }


    private boolean hasSunk(Player defender, String shot) {
        String rowCol = String.format("%s%s", ROW_NAMES.indexOf(shot.charAt(0)),
                Integer.parseInt(shot.substring(1)) - 1);
        Character type = defender.occupiedMap().get(rowCol);
        if (type != null) defender.occupiedList().remove(type);
        return !defender.occupiedList().contains(type);
    }

    private void markOccupied(Player defender) {

        List<Ship> ships = defender.ocean().getShips();
        for (Ship ship : ships) {
            for (String coordinates : ship.coordinates()) {
                defender.occupiedMap().put(coordinates, ship.shipType().getIdentifier());
            }
        }
        defender.occupiedList().addAll(defender.occupiedMap().values());
    }

    private void refresh(Ocean ocean, String[][] cellArray, int row, int col, String val) {
        markCell(cellArray, row, col, val);
        ocean.draw(cellArray);
    }

    private boolean hit(Player defender, int row, int col) {
        return defender.ocean().getCells()[row][col].equals("O ") ||
                defender.ocean().getGameCells()[row][col].equals("X ");
    }

    private void markCell(String[][] cellArray, int row, int col, String val) {
        cellArray[row][col] = val;
    }

    private void markCells(String[][] cellArray) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cellArray[row][col].equals("O ")) cellArray[row][col] = "~ ";
            }
        }
    }
}
