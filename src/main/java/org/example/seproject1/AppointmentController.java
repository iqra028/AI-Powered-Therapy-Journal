package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    @Autowired
    private JavaMailSender emailSender;

    @PostMapping
    public ResponseEntity<?> createAppointment(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> payload) {

        try {
            String clientId = extractClientIdFromToken(token);
            String therapistId = payload.get("therapistId");
            String date = payload.get("date");
            String time = payload.get("time");
            String clientName = payload.get("clientName");
            String notes = payload.get("notes");

            // Check if therapist exists
            Optional<Therapist> therapistOpt = therapistRepository.findById(therapistId);
            if (!therapistOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Therapist not found"));
            }

            Therapist therapist = therapistOpt.get();

            // Check if slot is already booked
            List<Appointment> existing = appointmentRepository.findByTherapistIdAndDateAndTime(therapistId, date, time);
            if (!existing.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Time slot already booked"));
            }

            // Check if slot is unavailable
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
            appointment.setClientName(clientName);
            appointment.setTherapistName(therapist.getUsername()); // Using getUsername() from User class
            appointment.setDate(date);
            appointment.setTime(time);
            appointment.setNotes(notes);
            appointment.setStatus("SCHEDULED");

            Appointment savedAppointment = appointmentRepository.save(appointment);

            // Add to therapist's appointment list
            therapist.addAppointment(savedAppointment.getId());
            therapistRepository.save(therapist);

            // Send email notification to therapist
            sendAppointmentNotification(therapist.getEmail(), clientName, date, time, notes);

            return ResponseEntity.ok(savedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating appointment: " + e.getMessage()));
        }
    }

    /**
     * Updates the status of an appointment
     * @param appointmentId The ID of the appointment to update
     * @param payload The updated status information
     * @return ResponseEntity with the updated appointment or an error message
     */
    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable String appointmentId,
            @RequestBody Map<String, String> payload) {

        try {
            String status = payload.get("status");
            if (status == null || status.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }

            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (!appointmentOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(status.toUpperCase());
            Appointment updatedAppointment = appointmentRepository.save(appointment);

            // Notify client via email if needed
            // sendAppointmentStatusUpdateNotification(appointment.getClientEmail(), status, appointment.getDate(), appointment.getTime());

            return ResponseEntity.ok(updatedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating appointment status: " + e.getMessage()));
        }
    }

    /**
     * Get all appointments - can be filtered by status
     */
    @GetMapping
    public ResponseEntity<?> getAllAppointments(@RequestParam(required = false) String status) {
        try {
            List<Appointment> appointments;
            if (status != null && !status.isEmpty()) {
                // Add findByStatus method to AppointmentRepository
                // appointments = appointmentRepository.findByStatus(status.toUpperCase());
                // For now, we'll filter manually
                appointments = appointmentRepository.findAll();
                appointments.removeIf(appointment -> !status.equalsIgnoreCase(appointment.getStatus()));
            } else {
                appointments = appointmentRepository.findAll();
            }
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error retrieving appointments: " + e.getMessage()));
        }
    }

    private void sendAppointmentNotification(String therapistEmail, String clientName,
                                             String date, String time, String notes) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(therapistEmail);
            message.setSubject("New Appointment Booking Notification");

            String emailText = String.format(
                    "Dear Therapist,\n\n" +
                            "You have received a new appointment booking:\n\n" +
                            "Client Name: %s\n" +
                            "Date: %s\n" +
                            "Time: %s\n" +
                            "Notes: %s\n\n" +
                            "Please log in to your account to view more details.\n\n" +
                            "Best regards,\n" +
                            "The Therapy Platform Team",
                    clientName, date, time, (notes != null ? notes : "No additional notes")
            );

            message.setText(emailText);
            emailSender.send(message);

            System.out.println("Appointment notification email sent to: " + therapistEmail);
        } catch (Exception e) {
            System.err.println("Failed to send appointment notification email: " + e.getMessage());
            // Log the error but don't fail the appointment creation
        }
    }

    private String extractClientIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token.replace("client-auth-", "");
    }
}