package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
    // Add this to TherapistController.java
    @PostMapping("/mark-time-unavailable")
    public ResponseEntity<?> markTimeUnavailable(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> payload) {
        try {
            String therapistId = extractTherapistIdFromToken(token);
            String date = payload.get("date");
            String time = payload.get("time");
            boolean allDay = Boolean.parseBoolean(payload.get("allDay"));

            if (date == null || date.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Date is required"));
            }

            if (!allDay && (time == null || time.isEmpty())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Time is required for specific time slots"));
            }

            // Check if appointments exist for this time slot
            List<Appointment> existingAppointments = appointmentRepository.findByTherapistIdAndDateAndTime(
                    therapistId, date, time);
            if (!existingAppointments.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot mark time as unavailable: you have an existing appointment"));
            }

            // Create new unavailable slot
            UnavailableSlot unavailableSlot = allDay ?
                    new UnavailableSlot(therapistId, date, true) :
                    new UnavailableSlot(therapistId, date, time);
            unavailableSlotRepository.save(unavailableSlot);

            return ResponseEntity.ok(Map.of("message", "Time slot marked as unavailable successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error marking time as unavailable: " + e.getMessage()));
        }
    }
    @GetMapping("/available-slots")
    public ResponseEntity<?> getAvailableSlots(
            @RequestHeader("Authorization") String token,
            @RequestParam String date) {
        try {
            String therapistId = extractTherapistIdFromToken(token);

            // Get all unavailable slots for this therapist and date
            List<UnavailableSlot> unavailableSlots = unavailableSlotRepository.findByTherapistIdAndDate(therapistId, date);

            // Check if there's an all-day unavailable slot
            boolean isAllDayUnavailable = unavailableSlots.stream()
                    .anyMatch(UnavailableSlot::isAllDay);

            if (isAllDayUnavailable) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            // Generate all possible time slots (9am-5pm for example)
            List<String> allSlots = generateTimeSlots();

            // Filter out unavailable slots
            List<String> availableSlots = allSlots.stream()
                    .filter(slot -> unavailableSlots.stream()
                            .noneMatch(unavailable -> slot.equals(unavailable.getTimeSlot())))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving available slots: " + e.getMessage()));
        }
    }

    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 9; hour < 18; hour++) {
            slots.add(String.format("%02d:00", hour));
        }
        return slots;
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getTherapistProfile(@RequestParam String id) {
        try {
            Optional<Therapist> therapist = therapistService.getTherapistById(id);
            Optional<Profile> profile = profileRepository.findByTherapistId(id);

            if (therapist.isPresent() && profile.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("therapist", therapist.get());
                response.put("profile", profile.get());
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(404).body(Map.of("error", "Therapist not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/unavailable-slots")
    public ResponseEntity<?> getUnavailableSlots(@RequestParam String therapistId) {
        try {
            List<UnavailableSlot> unavailableSlots = unavailableSlotRepository.findByTherapistId(therapistId);
            return ResponseEntity.ok(unavailableSlots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving unavailable slots: " + e.getMessage()));
        }
    }

    @PostMapping("/{therapistId}/appointments")
    public ResponseEntity<?> addTherapistAppointment(
            @PathVariable String therapistId,
            @RequestBody Map<String, String> payload) {
        try {
            String appointmentId = payload.get("appointmentId");

            Optional<Therapist> therapistOptional = therapistService.getTherapistById(therapistId);
            if (!therapistOptional.isPresent()) {
                return ResponseEntity.status(404).body(Map.of("error", "Therapist not found"));
            }

            Therapist therapist = therapistOptional.get();
            List<String> appointments = therapist.getAppointments();
            if (appointments == null) {
                appointments = new ArrayList<>();
            }
            appointments.add(appointmentId);
            therapist.setAppointments(appointments);
            therapistService.registerTherapist(therapist);

            return ResponseEntity.ok(Map.of("message", "Appointment added to therapist"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating therapist: " + e.getMessage()));
        }
    }
}