package model;

/** Represents a single rider. Riders are compared by ID. */
public class Rider extends Person implements IRider {
    private String email;
    private String homeCity;

    public Rider(int riderId, String name, String email, String phoneNumber, String homeCity) {
        super(riderId, name, phoneNumber);
        setEmail(email);
        setHomeCity(homeCity);
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        this.email = email;
    }

    @Override
    public String getHomeCity() {
        return homeCity;
    }

    @Override
    public void setHomeCity(String homeCity) {
        if (homeCity == null || homeCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Home city cannot be empty.");
        }
        this.homeCity = homeCity;
    }

    @Override
    public int compareTo(IRider other) {
        return Integer.compare(getId(), other.getId());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IRider && ((IRider) obj).getId() == getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Rider [" + super.toString() + " | Email: " + email
                + " | Home City: " + homeCity + "]";
    }
}