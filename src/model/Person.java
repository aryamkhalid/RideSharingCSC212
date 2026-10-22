package model;

/**
 * Abstract base class holding what Rider and Driver have in common:
 * ID, name, phone number and ride history. Cannot be instantiated directly.
 */
public abstract class Person implements IPerson {
    private final int id;                      // unique, never modified
    private String name;                       // full name
    private String phoneNumber;                // exactly 10 digits
    private final LinkedList<IRide> rideHistory; // rides of this person

    /**
     * @throws IllegalArgumentException if the name is empty or the phone is invalid
     */
    protected Person(int id, String name, String phoneNumber) {
        this.id = id;
        setName(name);
        setPhoneNumber(phoneNumber);
        this.rideHistory = new LinkedList<IRide>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("[0-9]{10}")) {
            throw new IllegalArgumentException(
                "Phone number must be exactly 10 digits (e.g., 0551234567).");
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public LinkedList<IRide> getRideHistory() {
        return rideHistory;
    }

    /** Appends a ride to this person's history. */
    public void addRideToHistory(IRide ride) {
        if (ride != null) {
            rideHistory.addLast(ride);
        }
    }

    /**
     * Removes the ride with the given ID from the history.
     * @return true if a ride was removed
     */
    public boolean removeRideFromHistory(int rideId) {
        if (rideHistory.empty()) {
            return false;
        }
        rideHistory.findFirst();
        while (true) {
            if (rideHistory.retrieve().getRideId() == rideId) {
                rideHistory.remove();
                return true;
            }
            if (rideHistory.last()) {
                return false;
            }
            rideHistory.findNext();
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Phone: " + phoneNumber;
    }
}
