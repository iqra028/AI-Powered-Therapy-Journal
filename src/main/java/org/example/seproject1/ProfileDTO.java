package org.example.seproject1;

import java.util.List;

public class ProfileDTO {
    private String fullName;
    private String specialty;
    private String bio;
    private String education;
    private List<String> specialties;
    private List<String> insuranceAccepted;

    // Constructors
    public ProfileDTO() {}

    // Getters and setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public List<String> getInsuranceAccepted() {
        return insuranceAccepted;
    }

    public void setInsuranceAccepted(List<String> insuranceAccepted) {
        this.insuranceAccepted = insuranceAccepted;
    }
}