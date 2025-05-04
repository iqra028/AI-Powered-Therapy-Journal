package org.example.seproject1;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByTherapistId(String therapistId);
    long countByTherapistId(String therapistId);
}