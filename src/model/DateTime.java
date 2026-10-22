package model;

/** Immutable point in time (year, month, day, hour, minute). */
public class DateTime implements IDateTime {
    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;

    /** @throws IllegalArgumentException if any field is out of range */
    public DateTime(int year, int month, int day, int hour, int minute) {
        if (year < 1) {
            throw new IllegalArgumentException("Invalid year.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be in [1..12].");
        }
        if (day < 1 || day > daysInMonth(year, month)) {
            throw new IllegalArgumentException("Invalid day for this month.");
        }
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Hour must be in [0..23].");
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Minute must be in [0..59].");
        }
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    /** Number of days in the given month (handles leap years). */
    private static int daysInMonth(int year, int month) {
        switch (month) {
            case 2:
                boolean leap = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
                return leap ? 29 : 28;
            case 4: case 6: case 9: case 11:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Parses text in the format "MM/DD/YYYY HH:MM".
     * @throws IllegalArgumentException if the text is malformed or out of range
     */
    public static DateTime parse(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Date/time cannot be null.");
        }
        String[] parts = text.trim().split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Use the format MM/DD/YYYY HH:MM.");
        }
        String[] date = parts[0].split("/");
        String[] time = parts[1].split(":");
        if (date.length != 3 || time.length != 2) {
            throw new IllegalArgumentException("Use the format MM/DD/YYYY HH:MM.");
        }
        try {
            return new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]),
                    Integer.parseInt(date[1]), Integer.parseInt(time[0]),
                    Integer.parseInt(time[1]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Use the format MM/DD/YYYY HH:MM.");
        }
    }

    @Override
    public int getYear() { return year; }

    @Override
    public int getMonth() { return month; }

    @Override
    public int getDay() { return day; }

    @Override
    public int getHour() { return hour; }

    @Override
    public int getMinute() { return minute; }

    /** @return "MM/DD/YYYY HH:MM" */
    @Override
    public String format() {
        return String.format("%02d/%02d/%04d %02d:%02d", month, day, year, hour, minute);
    }

    /** Chronological order: year, month, day, hour, then minute. */
    @Override
    public int compareTo(IDateTime other) {
        int c = Integer.compare(year, other.getYear());
        if (c != 0) return c;
        c = Integer.compare(month, other.getMonth());
        if (c != 0) return c;
        c = Integer.compare(day, other.getDay());
        if (c != 0) return c;
        c = Integer.compare(hour, other.getHour());
        if (c != 0) return c;
        return Integer.compare(minute, other.getMinute());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IDateTime && compareTo((IDateTime) obj) == 0;
    }

    @Override
    public int hashCode() {
        return (((year * 12 + month) * 31 + day) * 24 + hour) * 60 + minute;
    }

    @Override
    public String toString() {
        return format();
    }
}
