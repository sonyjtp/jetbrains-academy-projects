package contacts;

import java.util.Scanner;

import static contacts.Utils.validateNumber;

class Contact {
    private String name;

    private String surname;
    private String phoneNumber;

    public Contact(String name, String surname, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
    }

    void edit() throws IllegalArgumentException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select a field (name, surname, number):");
        String choice = scanner.nextLine();
        switch (choice) {
            case "name" -> {
                System.out.println("Enter name");
                this.name = scanner.nextLine();
            }
            case "surname" -> {
                System.out.println("Enter surname");
                this.surname = scanner.nextLine();
            }
            case "number" -> {
                System.out.println("Enter number:");
                this.phoneNumber = validateNumber(scanner.nextLine());
            }
            default -> throw new IllegalArgumentException("Invalid!");
        }
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
