package org.example.seproject1;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByTherapistId(String therapistId);
    List<Appointment> findByClientId(String clientId);
    List<Appointment> findByTherapistIdAndDate(String therapistId, String date);
}