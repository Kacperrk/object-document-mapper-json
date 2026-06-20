package org.example.cli;

import org.example.model.Address;
import org.example.model.Person;
import org.example.odm.JsonOdmMapper;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleDemo {

    private final Scanner scanner = new Scanner(System.in);
    private final JsonOdmMapper mapper = new JsonOdmMapper();

    public void run() {
        while (true) {
            printMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> serializePersonToJsonFile();
                case "2" -> deserializePersonFromJsonFile();
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

    private void serializePersonToJsonFile() {
        Address address = new Address(
                "Warszawa",
                "Marszałkowska",
                10
        );

        Person person = new Person(
                "Jan",
                "Kowalski",
                25,
                true,
                3.5,
                address,
                List.of("Java", "SQL"),
                List.of(7, 14),
                List.of(true, false),
                List.of(4.0, 4.5),
                List.of(address),
                List.of(
                        List.of("Java", "Spring"),
                        List.of("Docker")
                ),
                Arrays.asList("A", null, "B"),
                List.of(),
                null,
                "Jan123",
                "haslo123",
                null,
                "Polska"
        );

        String jsonText = mapper.toJson(person);
        Path path = Path.of("person.json");

        mapper.writeToFile(person, path);

        System.out.println("\n\nObiekt Java:");
        System.out.println(person);

        System.out.println("\nJSON:");
        System.out.println(jsonText);

        System.out.println("\nZapisano do pliku");
    }

    private void deserializePersonFromJsonFile() {
        Path path = Path.of("person.json");
        Person person = mapper.fromFile(path, Person.class);

        System.out.println("\n\nOdczytany obiekt:");
        System.out.println(person);
    }
}
