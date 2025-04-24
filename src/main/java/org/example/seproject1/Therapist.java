package org.example.seproject1;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
@Document(collection = "therapist")
class Therapist extends User {
    private List<String> patientIds;
    private List<String> appointmentIds;

    public Therapist(String id, String username, String email, String password) {
        super(id, username, email, password, "Therapist");
        this.patientIds = new ArrayList<>();
        this.appointmentIds = new ArrayList<>();
    }

    public void addPatient(String patientId) {
        this.patientIds.add(patientId);
    }

    public List<String> getPatientIds() {
        return patientIds;
    }

    public List<String> getAppointmentIds() { return appointmentIds; }
    public void addAppointmentId(String appointmentId) { this.appointmentIds.add(appointmentId); }
}
