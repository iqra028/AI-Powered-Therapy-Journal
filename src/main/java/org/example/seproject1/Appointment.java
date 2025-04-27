package org.example.seproject1;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "appointments")
public class Appointment {
    @Id
    private String id;
    private String clientId;
    private String therapistId;
    private String clientName;
    private String therapistName;
    private String date;  // YYYY-MM-DD format
    private String time;  // HH:MM format
    private String notes;
    private String status; // SCHEDULED, COMPLETED, CANCELLED

    // Constructors
    public Appointment() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getTherapistId() { return therapistId; }
    public void setTherapistId(String therapistId) { this.therapistId = therapistId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getTherapistName() { return therapistName; }
    public void setTherapistName(String therapistName) { this.therapistName = therapistName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}