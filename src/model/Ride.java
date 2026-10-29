package model;

/**
 * Abstract base class of every ride. Holds the data common to private and
 * shared rides. Pickup/drop-off times are final: they can only be set in the
 * constructor. Cannot be instantiated directly.
 */
public abstract class Ride implements IRide {
    private static int nextRideId = 1;      // generator of unique ride IDs

    private final int rideId;               // unique, never modified
    private String pickupLocation;
    private final IDateTime pickupTime;     // never modified
    private final IDateTime dropoffTime;    // never modified
    private String dropoffLocation;
    private IDriver driver;

    /**
     * @throws IllegalArgumentException if a location is empty, a time is null,
     *         the drop-off is not after the pickup, or the driver is null
     */
    protected Ride(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                   String dropoffLocation, IDriver driver) {
        String pickup = checkLocation(pickupLocation);
        String dropoff = checkLocation(dropoffLocation);
        if (pickupTime == null || dropoffTime == null) {
            throw new IllegalArgumentException("Ride times cannot be null.");
        }
        if (pickupTime.compareTo(dropoffTime) >= 0) {
            throw new IllegalArgumentException("Drop-off must be after pickup.");
        }
        if (driver == null) {
            throw new IllegalArgumentException("A ride needs a driver.");
        }
        this.rideId = nextRideId++;          // only after all checks passed
        this.pickupLocation = pickup;
        this.pickupTime = pickupTime;
        this.dropoffTime = dropoffTime;
        this.dropoffLocation = dropoff;
        this.driver = driver;
    }

    /** Validates a location and returns it trimmed. */
    private static String checkLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }
        return location.trim();
    }

    /**
     * Returns a NEW list with every rider taking part in the given ride
     * (one rider for a private ride, all participants for a shared ride).
     */
    public static LinkedList<IRider> ridersOf(IRide ride) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        if (ride instanceof IPrivateRide) {
            IRider rider = ((IPrivateRide) ride).getRider();
            if (rider != null) {
                result.addLast(rider);
            }
        } else if (ride instanceof ISharedRide) {
            LinkedList<IRider> participants = ((ISharedRide) ride).getParticipants();
            if (!participants.empty()) {
                participants.findFirst();
                while (true) {
                    result.addLast(participants.retrieve());
                    if (participants.last()) {
                        break;
                    }
                    participants.findNext();
                }
            }
        }
        return result;
    }

    @Override
    public int getRideId() { return rideId; }

    @Override
    public String getPickupLocation() { return pickupLocation; }

    @Override
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = checkLocation(pickupLocation);
    }

    @Override
    public IDateTime getPickupTime() { return pickupTime; }

    @Override
    public IDateTime getDropoffTime() { return dropoffTime; }

    @Override
    public String getDropoffLocation() { return dropoffLocation; }

    @Override
    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = checkLocation(dropoffLocation);
    }

    @Override
    public IDriver getDriver() { return driver; }

    @Override
    public void setDriver(IDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("A ride needs a driver.");
        }
        this.driver = driver;
    }

    /** Alphabetical order by pickup location (case-insensitive). */
    @Override
    public int compareTo(IRide other) {
        return pickupLocation.compareToIgnoreCase(other.getPickupLocation());
    }

    /** Text shared by all ride types; subclasses add their riders. */
    protected String describe() {
        return "Ride #" + rideId + " | " + pickupLocation + " (" + pickupTime.format()
                + ") -> " + dropoffLocation + " (" + dropoffTime.format()
                + ") | Driver: " + driver.getName() + " (ID " + driver.getId() + ")";
    }
}
