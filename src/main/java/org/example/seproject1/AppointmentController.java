package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TherapistRepository therapistRepository;
    @Autowired
    private UnavailableSlotRepository unavailableSlotRepository;
    @PostMapping
    public ResponseEntity<?> createAppointment(

            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> payload) {

        try {

            String clientId = extractClientIdFromToken(token);
            String therapistId = payload.get("therapistId");
            String date = payload.get("date");
            String time = payload.get("time");

            // Check if slot is available
            Optional<Therapist> therapistOpt = therapistRepository.findById(therapistId);
            if (!therapistOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Therapist not found"));
            }

            // Check if slot is already booked
            List<Appointment> existing = appointmentRepository.findByTherapistIdAndDateAndTime(therapistId, date, time);
            if (!existing.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Time slot already booked"));
            }
            List<UnavailableSlot> unavailableSlots = unavailableSlotRepository.findByTherapistIdAndDate(therapistId, date);
            boolean isTimeUnavailable = unavailableSlots.stream()
                    .anyMatch(slot -> slot.isAllDay() || time.equals(slot.getTimeSlot()));

            if (isTimeUnavailable) {
                return ResponseEntity.badRequest().body(Map.of("error", "Time slot is marked as unavailable"));
            }
            // Create new appointment
            Appointment appointment = new Appointment();
            appointment.setClientId(clientId);
            appointment.setTherapistId(therapistId);
            appointment.setDate(date);
            appointment.setTime(time);
            appointment.setStatus("SCHEDULED");

            Appointment savedAppointment = appointmentRepository.save(appointment);

            // Add to therapist's appointment list
            Therapist therapist = therapistOpt.get();
            therapist.addAppointment(savedAppointment.getId());
            therapistRepository.save(therapist);

            return ResponseEntity.ok(savedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating appointment: " + e.getMessage()));
        }
    }

    private String extractClientIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token.replace("client-auth-", "");
    }
}