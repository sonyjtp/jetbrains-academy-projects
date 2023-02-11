package contacts;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        boolean exit = false;
        try {
            while (!exit) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter action (add, remove, edit, count, list, exit):");
                Action action = getAction(scanner.nextLine());
                if (action == null) {
                    throw new IllegalArgumentException("Invalid action!");
                }
                switch (action) {
                    case ADD ->  phoneBook.add(scanner);
                    case COUNT -> phoneBook.count();
                    case EDIT -> phoneBook.edit(scanner);
                    case EXIT -> exit = true;
                    case LIST -> phoneBook.list();
                    case REMOVE -> phoneBook.remove(scanner);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Action getAction(String actionStr) throws IllegalArgumentException{
        Action action = null;
        try {
            action = Action.valueOf(actionStr.toUpperCase());
        }catch (IllegalArgumentException e) {
            System.out.printf("Invalid argument: %s\n", actionStr);
        }
        return action;
    }
}

