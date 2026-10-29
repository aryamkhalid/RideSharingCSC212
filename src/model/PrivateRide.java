package model;

/** A ride with exactly one rider. */
public class PrivateRide extends Ride implements IPrivateRide {
    private IRider rider;

    /** @throws IllegalArgumentException if the rider is null or the base data is invalid */
    public PrivateRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                       String dropoffLocation, IDriver driver, IRider rider) {
        super(pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
        if (rider == null) {
            throw new IllegalArgumentException("A private ride needs a rider.");
        }
        this.rider = rider;
    }

    @Override
    public IRider getRider() { return rider; }

    @Override
    public void setRider(IRider rider) {
        if (rider == null) {
            throw new IllegalArgumentException("A private ride needs a rider.");
        }
        this.rider = rider;
    }

    @Override
    public boolean hasRider(int riderId) {
        return rider.getId() == riderId;
    }

    @Override
    public String toString() {
        return "[PRIVATE] " + describe() + " | Rider: " + rider.getName()
                + " (ID " + rider.getId() + ")";
    }
}
