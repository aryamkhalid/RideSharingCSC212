package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RideSharingSystem {
    private final RiderList riders = new RiderList();
    private final DriverList drivers = new DriverList();

    public RiderList getRiders() {
        return riders;
    }

    public DriverList getDrivers() {
        return drivers;
    }

    /**
     * Loads drivers from a CSV file. Format expected: id, name, phone, plate, type
     */
    public boolean loadDrivers(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip header if present
                    if (line.toLowerCase().contains("id")) continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String phone = parts[2].trim();
                    String plate = parts[3].trim();
                    VehicleType type = Driver.parseVehicleType(parts[4].trim());
                    
                    Driver driver = new Driver(id, name, phone, plate, type);
                    drivers.add(driver);
                }
            }
            return true;
        } catch (IOException | NumberFormatException e) {
            return false;
        }
    }

    /**
     * Loads riders from a CSV file. Format expected: id, name, email, phone, city
     */
    public boolean loadRiders(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().contains("id")) continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    String phone = parts[3].trim();
                    String city = parts[4].trim();
                    
                    Rider rider = new Rider(id, name, email, phone, city);
                    riders.add(rider);
                }
            }
            return true;
        } catch (IOException | NumberFormatException e) {
            return false;
        }
    }
}