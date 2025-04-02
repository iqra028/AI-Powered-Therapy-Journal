package org.example.seproject1;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {
    List<JournalEntry> findByUserId(String userId);
    List<JournalEntry> findByUserIdAndType(String userId, String type);
    List<JournalEntry> findByUserIdAndMood(String userId, String mood);
    List<JournalEntry> findByUserIdOrderByCreatedAtDesc(String userId);
    List<JournalEntry> findByUserIdAndPrivacy(String userId, String privacy);
}