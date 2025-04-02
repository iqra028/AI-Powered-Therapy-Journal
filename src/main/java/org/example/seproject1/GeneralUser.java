package org.example.seproject1;

import java.util.ArrayList;
import java.util.List;

public class GeneralUser extends User{
    private List<String> journalIds;
    private List<String> appointmentIds;

    public GeneralUser(String id, String username, String email, String password) {
        super(id, username, email, password, "GeneralUser");
        this.journalIds = new ArrayList<>();
        this.appointmentIds = new ArrayList<>();
    }

    public List<String> getJournalIds() { return journalIds; }
    public void addJournalId(String journalId) { this.journalIds.add(journalId); }

    public List<String> getAppointmentIds() { return appointmentIds; }
    public void addAppointmentId(String appointmentId) { this.appointmentIds.add(appointmentId); }
}
