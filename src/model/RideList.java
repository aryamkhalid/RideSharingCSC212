package model;

/** Linked list of rides, always kept in alphabetical order by pickup location. */
public class RideList implements IRideList {
    private final LinkedList<IRide> rides = new LinkedList<IRide>();

    /**
     * Inserts the ride in sorted position (after rides with the same pickup
     * location). Fails if the ride is null or its ID is already stored. O(n).
     */
    @Override
    public boolean addRide(IRide ride) {
        if (ride == null || containsId(ride.getRideId())) {
            return false;
        }
        if (rides.empty()) {
            rides.insertAtFirst(ride);
            return true;
        }
        rides.findFirst();
        while (true) {
            if (ride.compareTo(rides.retrieve()) < 0) {
                rides.insertBeforeCurrent(ride);
                return true;
            }
            if (rides.last()) {
                rides.insert(ride);      // goes after the last node
                return true;
            }
            rides.findNext();
        }
    }

    /** Removes the ride with the given ID. O(n). */
    @Override
    public boolean removeRideById(int rideId) {
        if (rides.empty()) {
            return false;
        }
        rides.findFirst();
        while (true) {
            if (rides.retrieve().getRideId() == rideId) {
                rides.remove();
                return true;
            }
            if (rides.last()) {
                return false;
            }
            rides.findNext();
        }
    }

    /** @return a new list with all rides, already sorted. O(n). */
    @Override
    public LinkedList<IRide> getAllAlphabetically() {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (rides.empty()) {
            return result;
        }
        rides.findFirst();
        while (true) {
            result.addLast(rides.retrieve());
            if (rides.last()) {
                break;
            }
            rides.findNext();
        }
        return result;
    }

    /** @return all rides whose pickup location matches (ignoring case). */
    @Override
    public LinkedList<IRide> findByPickupLocation(String pickupLocation) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (pickupLocation == null || rides.empty()) {
            return result;
        }
        rides.findFirst();
        while (true) {
            IRide r = rides.retrieve();
            if (r.getPickupLocation().equalsIgnoreCase(pickupLocation.trim())) {
                result.addLast(r);
            }
            if (rides.last()) {
                break;
            }
            rides.findNext();
        }
        return result;
    }

    /** @return all rides that include a rider whose full name matches exactly. */
    @Override
    public LinkedList<IRide> findByRiderName(String riderFullName) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (riderFullName == null || rides.empty()) {
            return result;
        }
        rides.findFirst();
        while (true) {
            IRide r = rides.retrieve();
            if (hasRiderNamed(Ride.ridersOf(r), riderFullName.trim())) {
                result.addLast(r);
            }
            if (rides.last()) {
                break;
            }
            rides.findNext();
        }
        return result;
    }

    @Override
    public int size() { return rides.size(); }

    /** @return true if a ride with this ID is already stored. */
    private boolean containsId(int rideId) {
        if (rides.empty()) {
            return false;
        }
        rides.findFirst();
        while (true) {
            if (rides.retrieve().getRideId() == rideId) {
                return true;
            }
            if (rides.last()) {
                return false;
            }
            rides.findNext();
        }
    }

    /** @return true if any rider in the list has exactly this full name. */
    private boolean hasRiderNamed(LinkedList<IRider> list, String name) {
        if (list.empty()) {
            return false;
        }
        list.findFirst();
        while (true) {
            if (list.retrieve().getName().equals(name)) {
                return true;
            }
            if (list.last()) {
                return false;
            }
            list.findNext();
        }
    }
}
