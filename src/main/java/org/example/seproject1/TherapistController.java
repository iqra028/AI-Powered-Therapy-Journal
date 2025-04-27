package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    private ProfileRepository profileRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @GetMapping("/profile")
    public ResponseEntity<?> getTherapistProfile(@RequestHeader("Authorization") String token) {
        try {
            String therapistId = extractTherapistIdFromToken(token);
            Optional<Therapist> therapist = therapistService.getTherapistById(therapistId);

            if (therapist.isPresent()) {
                // Also check if profile exists

                Optional<Profile> profile = profileRepository.findByTherapistId(therapistId);
                Map<String, Object> response = new HashMap<>();
                response.put("therapist", therapist.get());
                response.put("profile", profile.orElse(null));
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(404).body(Map.of("error", "Therapist not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
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
            therapist.setUsername(updatedTherapist.getUsername());

            if (updatedTherapist.getPassword() != null && !updatedTherapist.getPassword().isEmpty()) {
                therapist.setPassword(updatedTherapist.getPassword());
            }

            Therapist savedTherapist = therapistService.registerTherapist(therapist);
            return ResponseEntity.ok(savedTherapist);
        }

        return ResponseEntity.status(404).body(Map.of("error", "Therapist not found"));
    }

    // Get all appointments for a therapist (only one method for this endpoint)
    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointments(@RequestHeader("Authorization") String token) {
        String therapistId = extractTherapistIdFromToken(token);
        List<Appointment> appointments = appointmentRepository.findByTherapistId(therapistId);
        return ResponseEntity.ok(appointments);
    }

    // Helper method to extract therapist ID from token
    private String extractTherapistIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token.replace("therapist-auth-", "");
    }
    @Autowired
    private UnavailableSlotRepository unavailableSlotRepository;

    // Get all unavailable slots for a therapist
    @GetMapping("/unavailable-slots")
    public ResponseEntity<?> getUnavailableSlots(@RequestHeader("Authorization") String token) {
        try {
            String therapistId = extractTherapistIdFromToken(token);
            List<UnavailableSlot> unavailableSlots = unavailableSlotRepository.findByTherapistId(therapistId);
            return ResponseEntity.ok(unavailableSlots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving unavailable slots: " + e.getMessage()));
        }
    }

    // Mark a day as unavailable
    @PostMapping("/mark-day-unavailable")
    public ResponseEntity<?> markDayUnavailable(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> payload) {
        try {
            String therapistId = extractTherapistIdFromToken(token);
            String date = payload.get("date");

            if (date == null || date.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Date is required"));
            }

            // Check if appointments exist for this day
            List<Appointment> existingAppointments = appointmentRepository.findByTherapistIdAndDate(therapistId, date);
            if (!existingAppointments.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot mark day as unavailable: you have existing appointments"));
            }

            // Create new unavailable slot
            UnavailableSlot unavailableSlot = new UnavailableSlot(therapistId, date, true);
            unavailableSlotRepository.save(unavailableSlot);

            return ResponseEntity.ok(Map.of("message", "Day marked as unavailable successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error marking day as unavailable: " + e.getMessage()));
        }
    }
}