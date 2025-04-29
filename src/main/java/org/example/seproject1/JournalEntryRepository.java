package org.example.seproject1;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {
    List<JournalEntry> findByUserId(String userId);
    List<JournalEntry> findByUserIdAndType(String userId, String type);
    List<JournalEntry> findByUserIdAndMood(String userId, String mood);
    List<JournalEntry> findByPrivacyAndStatus(String privacy, String status);
    List<JournalEntry> findByStatus(String status);

    // New method to find journals by multiple status values
    List<JournalEntry> findByStatusIn(List<String> statuses);
    List<JournalEntry> findByUserIdAndDateAfter(String userId, Date date);

}