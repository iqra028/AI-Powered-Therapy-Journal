package org.example.seproject1;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentRepository {
    private final List<Appointment> appointmentList = new ArrayList<>();

    public List<Appointment> findByUserId(String userId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointmentList) {
            if (appointment.getUserId().equals(userId)) {
                result.add(appointment);
            }
        }
        return result;
    }

    public List<Appointment> findByTherapistId(String therapistId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointmentList) {
            if (appointment.getTherapistId().equals(therapistId)) {
                result.add(appointment);
            }
        }
        return result;
    }

    public List<Appointment> findByStatus(String status) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointmentList) {
            if (appointment.getStatus().equalsIgnoreCase(status)) {
                result.add(appointment);
            }
        }
        return result;
    }

    public Optional<Appointment> findById(String id) {
        return appointmentList.stream().filter(appointment -> appointment.getId().equals(id)).findFirst();
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(appointmentList);
    }

    public void save(Appointment appointment) {
        appointmentList.removeIf(a -> a.getId().equals(appointment.getId())); // Remove if exists
        appointmentList.add(appointment);
    }

    public void delete(String id) {
        appointmentList.removeIf(appointment -> appointment.getId().equals(id));
    }
}
