package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TherapistService {

    @Autowired
    private TherapistRepository therapistRepository;
    @Autowired
    private ProfileRepository profileRepository;

    // Register a new therapist
    public Therapist registerTherapist(Therapist therapist) {
        // Generate a unique ID if not provided
        if (therapist.getId() == null || therapist.getId().isEmpty()) {
            therapist.setId(UUID.randomUUID().toString());
        }
        return therapistRepository.save(therapist);
    }

    // Find a therapist by email
    public Optional<Therapist> findByEmail(String email) {
        return therapistRepository.findByEmail(email);
    }

    // Find a therapist by username
    public Optional<Therapist> findByUsername(String username) {
        return therapistRepository.findByUsername(username);
    }

    // Get a therapist by ID
    public Optional<Therapist> getTherapistById(String id) {
        return therapistRepository.findById(id);
    }

    // Get all therapists
    public List<Therapist> getAllTherapists() {
        return therapistRepository.findAll();
    }

    // Update a therapist
    public Therapist updateTherapist(String id, Therapist updatedTherapist) {
        return therapistRepository.findById(id)
                .map(therapist -> {
                    therapist.setUsername(updatedTherapist.getUsername());
                    therapist.setEmail(updatedTherapist.getEmail());
                    therapist.setPassword(updatedTherapist.getPassword());
                    return therapistRepository.save(therapist);
                })
                .orElseThrow(() -> new RuntimeException("Therapist not found with id: " + id));
    }

    // Delete a therapist
    public void deleteTherapist(String id) {
        therapistRepository.deleteById(id);
    }
    public Profile createProfile(String therapistId, Profile profile) {
        profile.setTherapistId(therapistId);
        profile.setApproved(false); // Needs admin approval
        profile.setRating(0);
        profile.setReviewCount(0);
        return profileRepository.save(profile);
    }

    public Optional<Profile> getProfileByTherapistId(String therapistId) {
        return profileRepository.findByTherapistId(therapistId);
    }

    public List<Profile> getPendingProfiles() {
        return profileRepository.findByApproved(false);
    }

    public Profile approveProfile(String profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setApproved(true);
        return profileRepository.save(profile);
    }

    public void rejectProfile(String profileId) {
        profileRepository.deleteById(profileId);
    }
}
