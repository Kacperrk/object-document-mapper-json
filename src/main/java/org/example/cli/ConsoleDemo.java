package org.example.cli;

import org.example.model.Address;
import org.example.model.Person;

import java.util.List;
import java.util.Scanner;

public class ConsoleDemo {

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            printMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> showArchitectureInfo();
                case "2" -> showSampleObject();
                case "q" -> {
                    System.out.println("\nKoniec programu.");
                    return;
                }
                default -> System.out.println("\nNieprawidłowa opcja.");
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("\n==== ODM - projekt architektury ====");
        System.out.println("1 - Pokaż architekturę projektu");
        System.out.println("2 - Pokaż przykładowy model danych");
        System.out.println("q - Wyjście");
        System.out.print("Wybierz opcję: ");
    }

    private void showArchitectureInfo() {
        System.out.println("\nArchitektura projektu:");
        System.out.println("- cli: interfejs konsolowy");
        System.out.println("- model: klasy domenowe");
        System.out.println("- util: klasy pomocnicze");
        System.out.println("- exception: obsługa wyjątków");
        System.out.println("\nImplementacja właściwego mapowania zostanie dodana w kolejnych tygodniach.");
    }

    private void showSampleObject() {
        Person person = new Person(
                "Jan",
                22,
                true,
                new Address("Warszawa", "Polna 10", "00-001"),
                List.of("Java", "JSON", "Reflection")
        );

        System.out.println("\nPrzykładowy obiekt:");
        System.out.println(person);
    }
}