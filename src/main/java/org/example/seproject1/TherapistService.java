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
}
