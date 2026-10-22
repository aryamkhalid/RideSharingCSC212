package model;

/** A driver in the Ride-Sharing System. Compared by driver ID. */
public class Driver extends Person implements IDriver {
    private String vehiclePlate;     // 3 uppercase letters + 4 digits
    private VehicleType vehicleType; // type of the vehicle

    /**
     * Parameter order follows the CSV file: id, name, phone, plate, type.
     * @throws IllegalArgumentException on invalid data
     */
    public Driver(int driverId, String name, String phoneNumber,
                  String vehiclePlate, VehicleType vehicleType) {
        super(driverId, name, phoneNumber);
        setVehiclePlate(vehiclePlate);
        setVehicleType(vehicleType);
    }

    @Override
    public String getVehiclePlate() {
        return vehiclePlate;
    }

    @Override
    public void setVehiclePlate(String vehiclePlate) {
        if (vehiclePlate == null || !vehiclePlate.matches("[A-Z]{3}[0-9]{4}")) {
            throw new IllegalArgumentException(
                "Vehicle plate must be 3 uppercase letters followed by 4 digits (e.g., ABC1234).");
        }
        this.vehiclePlate = vehiclePlate;
    }

    @Override
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public void setVehicleType(VehicleType vehicleType) {
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null.");
        }
        this.vehicleType = vehicleType;
    }

    /**
     * Converts text such as "LUXURY SEDAN" / "luxury_sedan" / "suv" to a VehicleType.
     * Useful for the CSV loader and the menu.
     * @throws IllegalArgumentException if the text is not a valid type
     */
    public static VehicleType parseVehicleType(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null.");
        }
        String normalized = text.trim().toUpperCase().replace(' ', '_');
        try {
            return VehicleType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown vehicle type: " + text);
        }
    }

    /** Orders strictly by driver ID. */
    @Override
    public int compareTo(IDriver other) {
        return Integer.compare(getId(), other.getId());
    }

    /** Two drivers are equal if they have the same ID. */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IDriver && ((IDriver) obj).getId() == getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Driver [" + super.toString() + " | Plate: " + vehiclePlate
                + " | Vehicle: " + vehicleType + "]";
    }
}
