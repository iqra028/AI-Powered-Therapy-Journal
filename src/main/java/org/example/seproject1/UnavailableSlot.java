package org.example.seproject1;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "unavailable_slots")
public class UnavailableSlot {
    @Id
    private String id;
    private String therapistId;
    private String date;        // Format: YYYY-MM-DD
    private boolean allDay;     // true if entire day is unavailable
    private String timeSlot;    // Optional: specific time slot if not all day

    // Constructors
    public UnavailableSlot() {}

    public UnavailableSlot(String therapistId, String date, boolean allDay) {
        this.therapistId = therapistId;
        this.date = date;
        this.allDay = allDay;
    }

    public UnavailableSlot(String therapistId, String date, String timeSlot) {
        this.therapistId = therapistId;
        this.date = date;
        this.allDay = false;
        this.timeSlot = timeSlot;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTherapistId() { return therapistId; }
    public void setTherapistId(String therapistId) { this.therapistId = therapistId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean isAllDay() { return allDay; }
    public void setAllDay(boolean allDay) { this.allDay = allDay; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
}