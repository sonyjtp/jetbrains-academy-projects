package battleship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Battleship {
    public static void main(String[] args) {
        Player player1 = new Player("Player 1", new Ocean(), new HashMap<>(), new ArrayList<>());
        Player player2 = new Player("Player 2", new Ocean(), new HashMap<>(), new ArrayList<>());
        init(player1);
        System.out.println("Press Enter and pass the move to another player");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        init(player2);
        //System.out.println("The game starts!");
        Game game = new Game();
        game.play(List.of(player1, player2));
    }

    private static void init(Player player) {
        System.out.printf("%s, place your ships on the game field\n", player.name());
        player.ocean().prepareBattleGround();
    }
}
