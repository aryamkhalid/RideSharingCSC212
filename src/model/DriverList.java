package model;

/** Linked list of drivers, always kept sorted by driver ID. */
public class DriverList implements IDriverList {
    private final LinkedList<IDriver> drivers = new LinkedList<IDriver>();

    /** Inserts in sorted position; fails if the ID or the plate already exists. */
    @Override
    public boolean add(IDriver driver) {
        if (driver == null || findByVehiclePlate(driver.getVehiclePlate()) != null) {
            return false;                        // duplicate plate
        }
        if (drivers.empty()) {
            drivers.insertAtFirst(driver);
            return true;
        }
        drivers.findFirst();
        while (true) {
            int c = driver.compareTo(drivers.retrieve());
            if (c == 0) {
                return false;                    // duplicate ID
            }
            if (c < 0) {
                drivers.insertBeforeCurrent(driver);
                return true;
            }
            if (drivers.last()) {
                drivers.insert(driver);
                return true;
            }
            drivers.findNext();
        }
    }

    @Override
    public IDriver findById(int driverId) {
        if (drivers.empty()) {
            return null;
        }
        drivers.findFirst();
        while (true) {
            IDriver d = drivers.retrieve();
            if (d.getId() == driverId) {
                return d;
            }
            if (d.getId() > driverId || drivers.last()) {
                return null;
            }
            drivers.findNext();
        }
    }

    @Override
    public LinkedList<IDriver> findByName(String fullName) {
        return collect(fullName, null, true);
    }

    @Override
    public IDriver findByVehiclePlate(String vehiclePlate) {
        if (drivers.empty()) {
            return null;
        }
        drivers.findFirst();
        while (true) {
            IDriver d = drivers.retrieve();
            if (d.getVehiclePlate().equals(vehiclePlate)) {
                return d;
            }
            if (drivers.last()) {
                return null;
            }
            drivers.findNext();
        }
    }

    @Override
    public LinkedList<IDriver> findByVehicleType(VehicleType vehicleType) {
        return collect(null, vehicleType, false);
    }

    /** @return a new list with all drivers, sorted by ID. O(n). */
    @Override
    public LinkedList<IDriver> getAll() {
        LinkedList<IDriver> result = new LinkedList<IDriver>();
        if (drivers.empty()) {
            return result;
        }
        drivers.findFirst();
        while (true) {
            result.addLast(drivers.retrieve());
            if (drivers.last()) {
                break;
            }
            drivers.findNext();
        }
        return result;
    }

    @Override
    public boolean removeById(int driverId) {
        if (drivers.empty()) {
            return false;
        }
        drivers.findFirst();
        while (true) {
            IDriver d = drivers.retrieve();
            if (d.getId() == driverId) {
                drivers.remove();
                return true;
            }
            if (d.getId() > driverId || drivers.last()) {
                return false;
            }
            drivers.findNext();
        }
    }

    @Override
    public boolean removeByVehiclePlate(String vehiclePlate) {
        if (drivers.empty()) {
            return false;
        }
        drivers.findFirst();
        while (true) {
            if (drivers.retrieve().getVehiclePlate().equals(vehiclePlate)) {
                drivers.remove();
                return true;
            }
            if (drivers.last()) {
                return false;
            }
            drivers.findNext();
        }
    }

    /** Removes EVERY driver whose full name matches exactly. */
    @Override
    public int removeByName(String fullName) {
        return removeAll(fullName, null, true);
    }

    /** Removes EVERY driver with the given vehicle type. */
    @Override
    public int removeByVehicleType(VehicleType vehicleType) {
        return removeAll(null, vehicleType, false);
    }

    @Override
    public int size() {
        return drivers.size();
    }

    /** Checks a driver against the name (if byName) or the vehicle type. */
    private boolean matches(IDriver d, String name, VehicleType type, boolean byName) {
        return byName ? d.getName().equals(name) : d.getVehicleType() == type;
    }

    /** Collects all matching drivers (sorted by ID) into a new list. */
    private LinkedList<IDriver> collect(String name, VehicleType type, boolean byName) {
        LinkedList<IDriver> result = new LinkedList<IDriver>();
        if (drivers.empty()) {
            return result;
        }
        drivers.findFirst();
        while (true) {
            IDriver d = drivers.retrieve();
            if (matches(d, name, type, byName)) {
                result.addLast(d);
            }
            if (drivers.last()) {
                break;
            }
            drivers.findNext();
        }
        return result;
    }

    /** Traverses the WHOLE list and removes every match; returns the count. */
    private int removeAll(String name, VehicleType type, boolean byName) {
        int count = 0;
        if (drivers.empty()) {
            return 0;
        }
        drivers.findFirst();
        while (true) {
            boolean wasLast = drivers.last();
            if (matches(drivers.retrieve(), name, type, byName)) {
                drivers.remove();  // current now points to the next node
                count++;
                if (wasLast || drivers.empty()) {
                    break;
                }
            } else {
                if (wasLast) {
                    break;
                }
                drivers.findNext();
            }
        }
        return count;
    }
}
