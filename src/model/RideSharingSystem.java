package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Coordinates riders, drivers and rides and enforces the system rules:
 * uniqueness, existence checks, conflict-free scheduling and cascade deletion.
 * This class never prints; every search returns its result.
 */
public class RideSharingSystem implements IRideSharingSystem {
    private static final int KIND_RIDERS = 0;
    private static final int KIND_DRIVERS = 1;
    private static final int KIND_RIDES = 2;

    private final RiderList riders = new RiderList();
    private final DriverList drivers = new DriverList();
    private final RideList rides = new RideList();

    private int lastRejectedCount = 0;   // rows rejected by the last CSV load

    // ------------------------------------------------------------------
    // CSV loading
    // ------------------------------------------------------------------

    @Override
    public boolean loadRidersFromCSV(String ridersFilePath) {
        return loadCsv(ridersFilePath, KIND_RIDERS);
    }

    @Override
    public boolean loadDriversFromCSV(String driversFilePath) {
        return loadCsv(driversFilePath, KIND_DRIVERS);
    }

    @Override
    public boolean loadRidesFromCSV(String ridesFilePath) {
        return loadCsv(ridesFilePath, KIND_RIDES);
    }

    /** @return how many rows the most recent CSV load rejected (Main can show it). */
    public int getLastRejectedCount() {
        return lastRejectedCount;
    }

    /**
     * Reads a CSV file row by row. Valid rows are loaded, invalid rows are
     * skipped and counted.
     * @return true only if the file was read and NO row was rejected
     */
    private boolean loadCsv(String path, int kind) {
        lastRejectedCount = 0;
        if (path == null) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] fields = splitLine(line);
                if (firstLine) {
                    firstLine = false;
                    if (isHeader(kind, fields[0])) {
                        continue;
                    }
                }
                if (!addRow(kind, fields)) {
                    lastRejectedCount++;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return lastRejectedCount == 0;
    }

    /** Splits a row by comma (or by tab if the row has no comma) and trims each field. */
    private String[] splitLine(String line) {
        String[] fields = line.contains(",") ? line.split(",", -1) : line.split("\t", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        return fields;
    }

    /** @return true if the first field of the first row is a column title. */
    private boolean isHeader(int kind, String firstField) {
        if (kind == KIND_RIDES) {
            return firstField.equalsIgnoreCase("rideType");
        }
        return !firstField.matches("[0-9]+");
    }

    /** Builds one object from one CSV row and adds it. @return false if the row is invalid. */
    private boolean addRow(int kind, String[] f) {
        try {
            if (kind == KIND_RIDERS) {
                // riderId,name,email,phoneNumber,homeCity
                if (f.length != 5) {
                    return false;
                }
                return addRider(new Rider(Integer.parseInt(f[0]), f[1], f[2], f[3], f[4]));
            }
            if (kind == KIND_DRIVERS) {
                // driverId,name,phoneNumber,vehiclePlate,vehicleType
                if (f.length != 5) {
                    return false;
                }
                VehicleType type = Driver.parseVehicleType(f[4]);
                return addDriver(new Driver(Integer.parseInt(f[0]), f[1], f[2], f[3], type));
            }
            // rideType,pickupLocation,pickupDateTime,dropoffDateTime,dropoffLocation,driverId,riderIds
            if (f.length != 7) {
                return false;
            }
            IDateTime pickup = DateTime.parse(f[2]);
            IDateTime dropoff = DateTime.parse(f[3]);
            int driverId = Integer.parseInt(f[5]);
            String[] idTexts = f[6].split(";");
            int[] riderIds = new int[idTexts.length];
            for (int i = 0; i < idTexts.length; i++) {
                riderIds[i] = Integer.parseInt(idTexts[i].trim());
            }
            if (f[0].equalsIgnoreCase("PRIVATE")) {
                if (riderIds.length != 1) {
                    return false;
                }
                return schedulePrivateRide(f[1], pickup, dropoff, f[4], riderIds[0], driverId);
            }
            if (f[0].equalsIgnoreCase("SHARED")) {
                return scheduleSharedRide(f[1], pickup, dropoff, f[4], riderIds, driverId);
            }
            return false;                                // unknown ride type
        } catch (IllegalArgumentException e) {
            return false;   // bad number, bad date, bad phone/plate, bad vehicle type...
        }
    }

    // ------------------------------------------------------------------
    // Riders and drivers
    // ------------------------------------------------------------------

    @Override
    public boolean addRider(IRider rider) {
        return rider != null && riders.add(rider);   // RiderList rejects duplicate ID/email
    }

    @Override
    public boolean addDriver(IDriver driver) {
        return driver != null && drivers.add(driver); // DriverList rejects duplicate ID/plate
    }

    @Override
    public IRider searchRiderById(int riderId) { return riders.findById(riderId); }

    @Override
    public IRider searchRiderByEmail(String email) { return riders.findByEmail(email); }

    @Override
    public LinkedList<IRider> searchRidersByName(String fullName) {
        return riders.findByName(fullName);
    }

    @Override
    public LinkedList<IRider> searchRidersByHomeCity(String homeCity) {
        return riders.findByHomeCity(homeCity);
    }

    @Override
    public LinkedList<IRider> getAllRiders() { return riders.getAll(); }

    @Override
    public IDriver searchDriverById(int driverId) { return drivers.findById(driverId); }

    @Override
    public IDriver searchDriverByVehiclePlate(String vehiclePlate) {
        return drivers.findByVehiclePlate(vehiclePlate);
    }

    @Override
    public LinkedList<IDriver> searchDriversByVehicleType(VehicleType vehicleType) {
        return drivers.findByVehicleType(vehicleType);
    }

    @Override
    public LinkedList<IDriver> getAllDrivers() { return drivers.getAll(); }

    // ------------------------------------------------------------------
    // Cascade removal
    // ------------------------------------------------------------------

    /**
     * Removes a rider. Private rides of the rider are deleted; the rider is
     * taken off shared rides, and shared rides left empty are deleted.
     */
    @Override
    public boolean removeRider(int riderId) {
        IRider rider = riders.findById(riderId);
        if (rider == null) {
            return false;
        }
        // Work on a copy: the history list changes while we process it.
        LinkedList<IRide> history = copyOf(rider.getRideHistory());
        if (!history.empty()) {
            history.findFirst();
            while (true) {
                IRide ride = history.retrieve();
                if (ride instanceof ISharedRide) {
                    ISharedRide shared = (ISharedRide) ride;
                    shared.removeParticipantById(riderId);
                    removeFromHistory(rider, ride.getRideId());
                    if (shared.isEmpty()) {
                        deleteRide(ride);          // nobody left on this ride
                    }
                } else {
                    deleteRide(ride);              // private ride of this rider
                }
                if (history.last()) {
                    break;
                }
                history.findNext();
            }
        }
        return riders.removeById(riderId);
    }

    /** Removes a driver together with every ride (private or shared) assigned to them. */
    @Override
    public boolean removeDriver(int driverId) {
        IDriver driver = drivers.findById(driverId);
        if (driver == null) {
            return false;
        }
        LinkedList<IRide> history = copyOf(driver.getRideHistory());
        if (!history.empty()) {
            history.findFirst();
            while (true) {
                deleteRide(history.retrieve());
                if (history.last()) {
                    break;
                }
                history.findNext();
            }
        }
        return drivers.removeById(driverId);
    }

    /** Deletes a ride from the ride list and from the history of its driver and riders. */
    private void deleteRide(IRide ride) {
        rides.removeRideById(ride.getRideId());
        removeFromHistory(ride.getDriver(), ride.getRideId());
        LinkedList<IRider> ridersOfRide = Ride.ridersOf(ride);   // a copy
        if (ridersOfRide.empty()) {
            return;
        }
        ridersOfRide.findFirst();
        while (true) {
            removeFromHistory(ridersOfRide.retrieve(), ride.getRideId());
            if (ridersOfRide.last()) {
                break;
            }
            ridersOfRide.findNext();
        }
    }

    /** Removes the ride with this ID from a person's ride history. */
    private void removeFromHistory(IPerson person, int rideId) {
        LinkedList<IRide> history = person.getRideHistory();
        if (history.empty()) {
            return;
        }
        history.findFirst();
        while (true) {
            if (history.retrieve().getRideId() == rideId) {
                history.remove();
                return;
            }
            if (history.last()) {
                return;
            }
            history.findNext();
        }
    }

    // ------------------------------------------------------------------
    // Scheduling
    // ------------------------------------------------------------------

    /** Schedules a private ride if rider and driver exist and neither has a conflict. */
    @Override
    public boolean schedulePrivateRide(String pickupLocation, IDateTime pickupTime,
            IDateTime dropoffTime, String dropoffLocation, int riderId, int driverId) {
        if (!validTimes(pickupTime, dropoffTime)) {
            return false;
        }
        IRider rider = riders.findById(riderId);
        IDriver driver = drivers.findById(driverId);
        if (rider == null || driver == null) {
            return false;
        }
        if (hasConflict(rider, pickupTime, dropoffTime)
                || hasConflict(driver, pickupTime, dropoffTime)) {
            return false;
        }
        IRide ride;
        try {
            ride = new PrivateRide(pickupLocation, pickupTime, dropoffTime,
                    dropoffLocation, driver, rider);
        } catch (IllegalArgumentException e) {
            return false;                              // empty location, etc.
        }
        if (!rides.addRide(ride)) {
            return false;
        }
        rider.getRideHistory().addLast(ride);
        driver.getRideHistory().addLast(ride);
        return true;
    }

    /** Schedules a shared ride if all riders and the driver exist and nobody has a conflict. */
    @Override
    public boolean scheduleSharedRide(String pickupLocation, IDateTime pickupTime,
            IDateTime dropoffTime, String dropoffLocation, int[] riderIds, int driverId) {
        if (!validTimes(pickupTime, dropoffTime) || riderIds == null || riderIds.length == 0) {
            return false;
        }
        IDriver driver = drivers.findById(driverId);
        if (driver == null || hasConflict(driver, pickupTime, dropoffTime)) {
            return false;
        }
        IRider[] found = new IRider[riderIds.length];
        for (int i = 0; i < riderIds.length; i++) {
            for (int j = 0; j < i; j++) {
                if (riderIds[i] == riderIds[j]) {
                    return false;                      // same rider listed twice
                }
            }
            found[i] = riders.findById(riderIds[i]);
            if (found[i] == null || hasConflict(found[i], pickupTime, dropoffTime)) {
                return false;                          // missing rider or conflict
            }
        }
        SharedRide ride;
        try {
            ride = new SharedRide(pickupLocation, pickupTime, dropoffTime,
                    dropoffLocation, driver);
        } catch (IllegalArgumentException e) {
            return false;
        }
        for (int i = 0; i < found.length; i++) {
            ride.addParticipant(found[i]);
        }
        if (!rides.addRide(ride)) {
            return false;
        }
        driver.getRideHistory().addLast(ride);
        for (int i = 0; i < found.length; i++) {
            found[i].getRideHistory().addLast(ride);
        }
        return true;
    }

    /** @return true if both times exist and pickup is strictly before drop-off. */
    private boolean validTimes(IDateTime pickup, IDateTime dropoff) {
        return pickup != null && dropoff != null && pickup.compareTo(dropoff) < 0;
    }

    /**
     * Conflict detection: the new interval [pickup, dropoff) overlaps an existing
     * ride [p, d) exactly when pickup < d and p < dropoff.
     * Back-to-back rides (drop-off equals the next pickup) do not conflict.
     */
    private boolean hasConflict(IPerson person, IDateTime pickup, IDateTime dropoff) {
        LinkedList<IRide> history = person.getRideHistory();
        if (history.empty()) {
            return false;
        }
        history.findFirst();
        while (true) {
            IRide r = history.retrieve();
            if (pickup.compareTo(r.getDropoffTime()) < 0
                    && r.getPickupTime().compareTo(dropoff) < 0) {
                return true;
            }
            if (history.last()) {
                return false;
            }
            history.findNext();
        }
    }

    // ------------------------------------------------------------------
    // Ride searches
    // ------------------------------------------------------------------

    @Override
    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation) {
        return rides.findByPickupLocation(pickupLocation);
    }

    @Override
    public LinkedList<IRide> searchRidesByRiderName(String riderName) {
        return rides.findByRiderName(riderName);
    }

    /**
     * Riders of the shared ride(s) starting at this pickup location.
     * If several shared rides start there, their riders are combined (no duplicates).
     */
    @Override
    public LinkedList<IRider> getSharedRideParticipants(String pickupLocation) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        LinkedList<IRide> matches = rides.findByPickupLocation(pickupLocation);
        if (matches.empty()) {
            return result;
        }
        matches.findFirst();
        while (true) {
            IRide ride = matches.retrieve();
            if (ride instanceof ISharedRide) {
                LinkedList<IRider> people = Ride.ridersOf(ride);   // a copy
                if (!people.empty()) {
                    people.findFirst();
                    while (true) {
                        IRider r = people.retrieve();
                        if (!containsRider(result, r.getId())) {
                            result.addLast(r);
                        }
                        if (people.last()) {
                            break;
                        }
                        people.findNext();
                    }
                }
            }
            if (matches.last()) {
                break;
            }
            matches.findNext();
        }
        return result;
    }

    @Override
    public LinkedList<IRide> getAllRidesAlphabetically() {
        return rides.getAllAlphabetically();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** @return true if a rider with this ID is already in the list. */
    private boolean containsRider(LinkedList<IRider> list, int riderId) {
        if (list.empty()) {
            return false;
        }
        list.findFirst();
        while (true) {
            if (list.retrieve().getId() == riderId) {
                return true;
            }
            if (list.last()) {
                return false;
            }
            list.findNext();
        }
    }

    /** @return a new list holding the same elements (safe to traverse while the original changes). */
    private static <T> LinkedList<T> copyOf(LinkedList<T> source) {
        LinkedList<T> copy = new LinkedList<T>();
        if (source.empty()) {
            return copy;
        }
        source.findFirst();
        while (true) {
            copy.addLast(source.retrieve());
            if (source.last()) {
                break;
            }
            source.findNext();
        }
        return copy;
    }
}
