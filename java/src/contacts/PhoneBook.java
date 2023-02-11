package contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static contacts.Utils.validateNumber;

class PhoneBook {

    private final List<Contact> contacts;

    PhoneBook() {
        this.contacts = new ArrayList<>();
    }

    void add(Scanner scanner) {
        System.out.println("Enter the name:");
        String name = scanner.nextLine();
        System.out.println("Enter the surname:");
        String surname = scanner.nextLine();
        System.out.println("Enter the number:");
        this.contacts.add(new Contact(name, surname, validateNumber(scanner.nextLine())));
        System.out.println("The record added.");
    }

    void count() {
        System.out.printf("The Phone Book has %d records.\n", this.contacts.size());
    }

    void edit(Scanner scanner) {
        if (this.contacts.size() == 0) {
            System.out.println("No records to edit!");
        } else {
            list();
            System.out.println("Select a record: ");
            int index = scanner.nextInt() - 1;
            this.contacts.get(index).edit();
            System.out.println("The record updated!");
        }
    }

    void list() {
        for (Contact c : contacts) {
            System.out.printf(
                    "%d. %s %s, %s\n", contacts.indexOf(c) + 1,
                    c.getName(), c.getSurname(), c.getPhoneNumber()
            );
        }
    }

    void remove(Scanner scanner) {
        if (this.contacts.size() == 0) {
            System.out.println("No records to remove!");
        } else {
            list();
            System.out.println("Select a record: ");
            int choice = scanner.nextInt();
            this.contacts.remove(choice - 1);
            System.out.println("The record removed!");
        }
    }
}
