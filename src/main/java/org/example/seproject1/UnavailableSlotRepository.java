package org.example.seproject1;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnavailableSlotRepository extends MongoRepository<UnavailableSlot, String> {
    List<UnavailableSlot> findByTherapistId(String therapistId);
    List<UnavailableSlot> findByTherapistIdAndDate(String therapistId, String date);
}