package model;

/** A carpool ride with one or more riders. */
public class SharedRide extends Ride implements ISharedRide {
    private final LinkedList<IRider> participants = new LinkedList<IRider>();

    public SharedRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                      String dropoffLocation, IDriver driver) {
        super(pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
    }

    @Override
    public LinkedList<IRider> getParticipants() { return participants; }

    /** @return false if the rider is null or already on this ride */
    @Override
    public boolean addParticipant(IRider rider) {
        if (rider == null || hasRider(rider.getId())) {
            return false;
        }
        participants.addLast(rider);
        return true;
    }

    /** @return true if a participant with that ID was found and removed */
    @Override
    public boolean removeParticipantById(int riderId) {
        if (participants.empty()) {
            return false;
        }
        participants.findFirst();
        while (true) {
            if (participants.retrieve().getId() == riderId) {
                participants.remove();
                return true;
            }
            if (participants.last()) {
                return false;
            }
            participants.findNext();
        }
    }

    @Override
    public boolean isEmpty() { return participants.empty(); }

    @Override
    public boolean hasRider(int riderId) {
        if (participants.empty()) {
            return false;
        }
        participants.findFirst();
        while (true) {
            if (participants.retrieve().getId() == riderId) {
                return true;
            }
            if (participants.last()) {
                return false;
            }
            participants.findNext();
        }
    }

    @Override
    public String toString() {
        String names = "";
        if (!participants.empty()) {
            participants.findFirst();
            while (true) {
                IRider r = participants.retrieve();
                names += r.getName() + " (ID " + r.getId() + ")";
                if (participants.last()) {
                    break;
                }
                names += ", ";
                participants.findNext();
            }
        }
        return "[SHARED] " + describe() + " | Riders: " + names;
    }
}
