package org.example.seproject1;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "therapist")
public class Therapist extends User {
    private List<String> patientIds;
    private List<String> appointment;

    public Therapist(String id, String username, String email, String password) {
        super(id, username, email, password, "Therapist");
        this.patientIds = new ArrayList<>();
        this.appointment = new ArrayList<>();
    }

    public void addPatient(String patientId) {
        if (!this.patientIds.contains(patientId)) {
            this.patientIds.add(patientId);
        }
    }

    public void addAppointment(String appointmentId) {
        if (!this.appointment.contains(appointmentId)) {
            this.appointment.add(appointmentId);
        }
    }

    public List<String> getPatientIds() {
        return patientIds;
    }

    public List<String> getAppointments() {
        return appointment;
    }

    public void removeAppointment(String appointmentId) {
        this.appointment.remove(appointmentId);
    }
    public void setAppointments(List<String> appointments) {
        this.appointment = appointments;
    }
}