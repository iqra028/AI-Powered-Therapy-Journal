package org.example.seproject1;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profiles")
public class Profile {
    @Id
    private String id;
    private String therapistId;
    private String fullName;
    private String specialty;
    private String bio;
    private String education;
    private String[] specialties;
    private String[] insuranceAccepted;
    private String[] availableSlots;
    private boolean approved;
    private double rating;
    private int reviewCount;

    // Constructors, getters, and setters
    public Profile() {}

    // Getters and setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTherapistId() { return therapistId; }
    public void setTherapistId(String therapistId) { this.therapistId = therapistId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    public String[] getSpecialties() { return specialties; }
    public void setSpecialties(String[] specialties) { this.specialties = specialties; }
    public String[] getInsuranceAccepted() { return insuranceAccepted; }
    public void setInsuranceAccepted(String[] insuranceAccepted) { this.insuranceAccepted = insuranceAccepted; }
    public String[] getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(String[] availableSlots) { this.availableSlots = availableSlots; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
}