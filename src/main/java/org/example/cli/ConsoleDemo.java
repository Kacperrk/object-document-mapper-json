package org.example.cli;

import org.example.model.Person;
import org.example.odm.JsonOdmMapper;

import java.nio.file.Path;
import java.util.Scanner;

public class ConsoleDemo {

    private final Scanner scanner = new Scanner(System.in);
    private final JsonOdmMapper mapper = new JsonOdmMapper();

    public void run() {
        while (true) {
            printMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> serialization();
                case "2" -> deserialization();
                case "q" -> {
                    return;
                }
                default -> System.out.println("\nNieprawidłowa opcja.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n\n=======================================");
        System.out.println("1 - Zapisz obiekt Person do JSON");
        System.out.println("2 - Odczytaj obiekt Person z JSON");
        System.out.println("q - Wyjście");
        System.out.print("Wybierz opcję: ");
    }

    private void serialization() {
        Person person = new Person("Jan", 22);

        String json = mapper.toJson(person);
        Path path = Path.of("person.json");

        mapper.writeToFile(person, path);

        System.out.println("\n\nObiekt Java:");
        System.out.println(person);

        System.out.println("\nJSON:");
        System.out.println(json);

        System.out.println("\nZapisano do pliku");
    }

    private void deserialization() {
        Path path = Path.of("person.json");
        Person person = mapper.fromFile(path, Person.class);

        System.out.println("\n\nOdczytany obiekt:");
        System.out.println(person);
    }
}
