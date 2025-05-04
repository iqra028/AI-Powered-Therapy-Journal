package org.example.seproject1;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

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
    private long reviewCount;
    private List<Feedback> feedbacks = new ArrayList<>();

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
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }


    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }



    private void calculateRating() {
        if (feedbacks.isEmpty()) {
            this.rating = 0;
            this.reviewCount = 0;
            return;
        }

        double sum = feedbacks.stream().mapToDouble(Feedback::getRating).sum();
        this.rating = sum / feedbacks.size();
        this.reviewCount = feedbacks.size();
    }
    public void addFeedback(Feedback feedback) {
        this.feedbacks.add(feedback);

        // Recalculate rating
        if (feedback.getRating() > 0) {
            double total = feedbacks.stream()
                    .mapToInt(Feedback::getRating)
                    .sum();
            this.rating = total / feedbacks.size();
            this.reviewCount = feedbacks.size();
        }
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(long reviewCount) {
        this.reviewCount = reviewCount;
    }
}