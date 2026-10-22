package model;

/** Stores all riders in a structure maintained in sorted order by rider ID. */
public class RiderList implements IRiderList {
    private final LinkedList<IRider> riders = new LinkedList<IRider>();

    @Override
    public boolean add(IRider rider) {
        if (rider == null || findByEmail(rider.getEmail()) != null) {
            return false;
        }
        if (riders.empty()) {
            riders.insertAtFirst(rider);
            return true;
        }
        riders.findFirst();
        while (true) {
            int c = rider.compareTo(riders.retrieve());
            if (c == 0) {
                return false;
            }
            if (c < 0) {
                riders.insertBeforeCurrent(rider);
                return true;
            }
            if (riders.last()) {
                riders.insert(rider);
                return true;
            }
            riders.findNext();
        }
    }

    @Override
    public IRider findById(int riderId) {
        if (riders.empty()) {
            return null;
        }
        riders.findFirst();
        while (true) {
            IRider r = riders.retrieve();
            if (r.getId() == riderId) {
                return r;
            }
            if (r.getId() > riderId || riders.last()) {
                return null;
            }
            riders.findNext();
        }
    }

    @Override
    public LinkedList<IRider> findByName(String fullName) {
        return collect(fullName, null, true);
    }

    @Override
    public IRider findByEmail(String email) {
        if (riders.empty()) {
            return null;
        }
        riders.findFirst();
        while (true) {
            IRider r = riders.retrieve();
            if (r.getEmail().equalsIgnoreCase(email)) {
                return r;
            }
            if (riders.last()) {
                return null;
            }
            riders.findNext();
        }
    }

    @Override
    public LinkedList<IRider> findByHomeCity(String homeCity) {
        return collect(homeCity, null, false);
    }

    @Override
    public LinkedList<IRider> getAll() {
        LinkedList<IRider> result = new LinkedList<IRider>();
        if (riders.empty()) {
            return result;
        }
        riders.findFirst();
        while (true) {
            result.addLast(riders.retrieve());
            if (riders.last()) {
                break;
            }
            riders.findNext();
        }
        return result;
    }

    @Override
    public boolean removeById(int riderId) {
        if (riders.empty()) {
            return false;
        }
        riders.findFirst();
        while (true) {
            IRider r = riders.retrieve();
            if (r.getId() == riderId) {
                riders.remove();
                return true;
            }
            if (r.getId() > riderId || riders.last()) {
                return false;
            }
            riders.findNext();
        }
    }

    @Override
    public boolean removeByEmail(String email) {
        if (riders.empty()) {
            return false;
        }
        riders.findFirst();
        while (true) {
            if (riders.retrieve().getEmail().equalsIgnoreCase(email)) {
                riders.remove();
                return true;
            }
            if (riders.last()) {
                return false;
            }
            riders.findNext();
        }
    }

    @Override
    public int removeByName(String fullName) {
        return removeAll(fullName, null, true);
    }

    @Override
    public int removeByHomeCity(String homeCity) {
        return removeAll(homeCity, null, false);
    }

    @Override
    public int size() {
        return riders.size();
    }

    private boolean matches(IRider r, String val, boolean byName) {
        return byName ? r.getName().equals(val) : r.getHomeCity().equalsIgnoreCase(val);
    }

    private LinkedList<IRider> collect(String val, String dummy, boolean byName) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        if (riders.empty()) {
            return result;
        }
        riders.findFirst();
        while (true) {
            IRider r = riders.retrieve();
            if (matches(r, val, byName)) {
                result.addLast(r);
            }
            if (riders.last()) {
                break;
            }
            riders.findNext();
        }
        return result;
    }

    private int removeAll(String val, String dummy, boolean byName) {
        int count = 0;
        if (riders.empty()) {
            return 0;
        }
        riders.findFirst();
        while (true) {
            boolean wasLast = riders.last();
            if (matches(riders.retrieve(), val, byName)) {
                riders.remove();
                count++;
                if (wasLast || riders.empty()) {
                    break;
                }
            } else {
                if (wasLast) {
                    break;
                }
                riders.findNext();
            }
        }
        return count;
    }
}