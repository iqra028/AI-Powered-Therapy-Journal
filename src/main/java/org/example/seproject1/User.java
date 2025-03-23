package org.example.seproject1;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String username;
    private String email;
    //private String password;
    private String role;
    private LocalDateTime createdAt;
    private String profileImageUrl;
    private List<String> journalIds = new ArrayList<>();
    private List<String> appointmentIds = new ArrayList<>();

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        //this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

   // public String getPassword() { return password; }
    //public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public List<String> getJournalIds() { return journalIds; }
    public void setJournalIds(List<String> journalIds) { this.journalIds = journalIds; }
    public void addJournalId(String journalId) { this.journalIds.add(journalId); }

    public List<String> getAppointmentIds() { return appointmentIds; }
    public void setAppointmentIds(List<String> appointmentIds) { this.appointmentIds = appointmentIds; }
    public void addAppointmentId(String appointmentId) { this.appointmentIds.add(appointmentId); }
}
