package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/therapist")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class TherapistController {

    @Autowired
    private TherapistService therapistService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Get therapist profile
    @GetMapping("/profile")
    public ResponseEntity<?> getTherapistProfile(@RequestHeader("Authorization") String token) {
        String therapistId = extractTherapistIdFromToken(token);
        Optional<Therapist> therapist = therapistService.getTherapistById(therapistId);

        if (therapist.isPresent()) {
            return ResponseEntity.ok(therapist.get());
        }

        return ResponseEntity.status(404).body(Map.of("error", "Therapist not found"));
    }

    // Update therapist profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateTherapistProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody Therapist updatedTherapist) {

        String therapistId = extractTherapistIdFromToken(token);
        Optional<Therapist> existingTherapist = therapistService.getTherapistById(therapistId);

        if (existingTherapist.isPresent()) {
            Therapist therapist = existingTherapist.get();

            // Update fields (only allow certain fields to be updated)
            therapist.setUsername(updatedTherapist.getUsername());
            // Don't allow email update through this endpoint for security

            // Update password only if provided
            if (updatedTherapist.getPassword() != null && !updatedTherapist.getPassword().isEmpty()) {
                therapist.setPassword(updatedTherapist.getPassword());
            }

            Therapist savedTherapist = therapistService.registerTherapist(therapist);
            return ResponseEntity.ok(savedTherapist);
        }

        return ResponseEntity.status(404).body(Map.of("error", "Therapist not found"));
    }

    // Get all appointments for a therapist
    @GetMapping("/appointments")
    public ResponseEntity<?> getTherapistAppointments(@RequestHeader("Authorization") String token) {
        String therapistId = extractTherapistIdFromToken(token);
        List<Appointment> appointments = appointmentRepository.findByTherapistId(therapistId);
        return ResponseEntity.ok(appointments);
    }

    // Helper method to extract therapist ID from token
    private String extractTherapistIdFromToken(String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // Remove "therapist-auth-" prefix
        return token.replace("therapist-auth-", "");
    }
}
