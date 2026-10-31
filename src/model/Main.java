
package model;

import java.util.Scanner;
import java.io.File;

public class Main {

    // Find CSV files even when the project is inside another folder
    private static String findCSVFile(String fileName) {
        String[] possiblePaths = {
            fileName,
            "RideSharingCSC212-main/" + fileName,
            "../" + fileName,
            "../RideSharingCSC212-main/" + fileName
        };

        for (String path : possiblePaths) {
            if (new File(path).isFile()) {
                return path;
            }
        }

        return fileName;
    }

    public static void main(String[] args) {
        RideSharingSystem system = new RideSharingSystem();
        Scanner scanner = new Scanner(System.in);

        // Load the CSV files
        boolean ridersLoaded = system.loadRidersFromCSV(
                findCSVFile("riders_100.csv"));

        boolean driversLoaded = system.loadDriversFromCSV(
                findCSVFile("drivers_30.csv"));

        boolean ridesLoaded = system.loadRidesFromCSV(
                findCSVFile("rides_40.csv"));

        System.out.println("=== Ride-Sharing System Started ===");
        System.out.println("Riders loaded: " + (ridersLoaded ? "Success" : "Failed"));
        System.out.println("Drivers loaded: " + (driversLoaded ? "Success" : "Failed"));
        System.out.println("Rides loaded: " + (ridesLoaded ? "Success" : "Failed"));

        int choice;

        do {
            System.out.println("\n--- Ride Sharing Menu ---");
            System.out.println("1. List all riders");
            System.out.println("2. Search for riders by home city");
            System.out.println("3. Search for riders by name");
            System.out.println("4. Add a rider");
            System.out.println("5. List all drivers");
            System.out.println("6. Search for drivers by vehicle type");
            System.out.println("7. Add a driver");
            System.out.println("8. List all rides alphabetically by pickup location");
            System.out.println("9. Search for rides by pickup location");
            System.out.println("10. Search for rides by rider name");
            System.out.println("11. List shared ride participants by pickup location");
            System.out.println("12. Add a ride");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            choice = readInt(scanner);

            switch (choice) {

                case 1:
                    printList(system.getAllRiders());
                    break;

                case 2:
                    System.out.print("Enter home city: ");
                    String city = scanner.nextLine();
                    printList(system.searchRidersByHomeCity(city));
                    break;

                case 3:
                    System.out.print("Enter rider full name: ");
                    String riderName = scanner.nextLine();
                    printList(system.searchRidersByName(riderName));
                    break;

                case 4:
                    try {
                        System.out.print("Enter rider ID: ");
                        int riderId = readInt(scanner);

                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();

                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();

                        System.out.print("Enter phone number: ");
                        String phone = scanner.nextLine();

                        System.out.print("Enter home city: ");
                        String homeCity = scanner.nextLine();

                        Rider rider = new Rider(riderId, name, email, phone, homeCity);

                        if (system.addRider(rider)) {
                            System.out.println("Rider added successfully.");
                        } else {
                            System.out.println("Failed to add rider.");
                        }

                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid rider data: " + e.getMessage());
                    }
                    break;

                case 5:
                    printList(system.getAllDrivers());
                    break;

                case 6:
                    try {
                        System.out.print("Enter vehicle type (SEDAN, LUXURY_SEDAN, SUV, VAN): ");
                        VehicleType type = Driver.parseVehicleType(scanner.nextLine());
                        printList(system.searchDriversByVehicleType(type));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid vehicle type.");
                    }
                    break;

                case 7:
                    try {
                        System.out.print("Enter driver ID: ");
                        int driverId = readInt(scanner);

                        System.out.print("Enter name: ");
                        String driverName = scanner.nextLine();

                        System.out.print("Enter phone number: ");
                        String driverPhone = scanner.nextLine();

                        System.out.print("Enter vehicle plate: ");
                        String plate = scanner.nextLine();

                        System.out.print("Enter vehicle type: ");
                        VehicleType vehicleType = Driver.parseVehicleType(scanner.nextLine());

                        Driver driver = new Driver(driverId, driverName,
                                driverPhone, plate, vehicleType);

                        if (system.addDriver(driver)) {
                            System.out.println("Driver added successfully.");
                        } else {
                            System.out.println("Failed to add driver.");
                        }

                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid driver data: " + e.getMessage());
                    }
                    break;

                case 8:
                    printList(system.getAllRidesAlphabetically());
                    break;

                case 9:
                    System.out.print("Enter pickup location: ");
                    String pickupSearch = scanner.nextLine();
                    printList(system.searchRidesByPickupLocation(pickupSearch));
                    break;

                case 10:
                    System.out.print("Enter rider full name: ");
                    String searchName = scanner.nextLine();
                    printList(system.searchRidesByRiderName(searchName));
                    break;

                case 11:
                    System.out.print("Enter pickup location: ");
                    String sharedLocation = scanner.nextLine();
                    printList(system.getSharedRideParticipants(sharedLocation));
                    break;

                case 12:
                    try {
                        System.out.print("Enter ride type (PRIVATE or SHARED): ");
                        String rideType = scanner.nextLine().trim();

                        if (!rideType.equalsIgnoreCase("PRIVATE")
                                && !rideType.equalsIgnoreCase("SHARED")) {
                            System.out.println("Invalid ride type.");
                            break;
                        }

                        System.out.print("Enter pickup location: ");
                        String pickup = scanner.nextLine();

                        System.out.print("Enter pickup time (MM/DD/YYYY HH:MM): ");
                        IDateTime pickupTime = DateTime.parse(scanner.nextLine());

                        System.out.print("Enter dropoff time (MM/DD/YYYY HH:MM): ");
                        IDateTime dropoffTime = DateTime.parse(scanner.nextLine());

                        System.out.print("Enter dropoff location: ");
                        String dropoff = scanner.nextLine();

                        System.out.print("Enter driver ID: ");
                        int assignedDriver = readInt(scanner);

                        boolean added;

                        if (rideType.equalsIgnoreCase("PRIVATE")) {
                            System.out.print("Enter rider ID: ");
                            int assignedRider = readInt(scanner);

                            added = system.schedulePrivateRide(
                                    pickup, pickupTime, dropoffTime,
                                    dropoff, assignedRider, assignedDriver);

                        } else {
                            System.out.print("Enter number of riders: ");
                            int count = readInt(scanner);

                            if (count < 2) {
                                System.out.println("A shared ride needs at least two riders.");
                                break;
                            }

                            int[] riderIds = new int[count];

                            for (int i = 0; i < count; i++) {
                                System.out.print("Enter rider ID " + (i + 1) + ": ");
                                riderIds[i] = readInt(scanner);
                            }

                            added = system.scheduleSharedRide(
                                    pickup, pickupTime, dropoffTime,
                                    dropoff, riderIds, assignedDriver);
                        }

                        if (added) {
                            System.out.println("Ride scheduled successfully.");
                        } else {
                            System.out.println("Failed to schedule ride.");
                        }

                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid ride data: " + e.getMessage());
                    }
                    break;

                case 0:
                    System.out.println("Exiting system. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number from 0 to 12.");
            }

        } while (choice != 0);

        scanner.close();
    }

    // Read an integer from the user
    private static int readInt(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Try again: ");
            }
        }
    }

    // Display the elements of our linked list
    private static <T> void printList(LinkedList<T> list) {
        if (list.empty()) {
            System.out.println("No results found.");
            return;
        }

        list.findFirst();

        while (true) {
            System.out.println(list.retrieve());

            if (list.last()) {
                break;
            }

            list.findNext();
        }
    }
}
