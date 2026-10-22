package model;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RideSharingSystem system = new RideSharingSystem();
        Scanner scanner = new Scanner(System.in);

        // تحميل ملفات الـ CSV الثلاثة عند بداية التشغيل
        boolean ridersLoaded = system.loadRiders("riders_100.csv");
        boolean driversLoaded = system.loadDrivers("drivers_30.csv");
        
        System.out.println("=== Ride-Sharing System Started ===");
        System.out.println("Riders loaded: " + (ridersLoaded ? "Success" : "Failed / File not found"));
        System.out.println("Drivers loaded: " + (driversLoaded ? "Success" : "Failed / File not found"));

        int choice = 0;
        do {
            System.out.println("\n--- Ride Sharing Menu ---");
            System.out.println("1. Display All Riders");
            System.out.println("2. Display All Drivers");
            System.out.println("3. Find Rider by ID");
            System.out.println("4. Find Driver by ID");
            System.out.println("5. Add New Rider");
            System.out.println("6. Add New Driver");
            System.out.println("7. Remove Rider by ID");
            System.out.println("8. Remove Driver by ID");
            System.out.println("9. Search Riders by City");
            System.out.println("10. Search Drivers by Vehicle Type");
            System.out.println("11. View System Statistics");
            System.out.println("12. Exit");
            System.out.print("Enter your choice (1-12): ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.println("Total Riders: " + system.getRiders().size());
                        break;
                    case 2:
                        System.out.println("Total Drivers: " + system.getDrivers().size());
                        break;
                    case 12:
                        System.out.println("Exiting system. Goodbye!");
                        break;
                    default:
                        System.out.println("Option under construction or not implemented yet.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number between 1 and 12.");
                scanner.next(); // Clear invalid token
            }
        } while (choice != 12);

        scanner.close();
    }
}