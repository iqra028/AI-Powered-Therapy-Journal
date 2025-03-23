package org.example.seproject1;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {

    // Find all journal entries by type (AI or Manual)
    List<JournalEntry> findByType(String type);

    // Find all journal entries by mood (Happy, Sad, etc.)
    List<JournalEntry> findByMood(String mood);

    // Find all journal entries ordered by creation date (Newest first)
    List<JournalEntry> findAllByOrderByCreatedAtDesc();

    // Find all public journal entries (assuming a 'privacy' field exists in JournalEntry)
    List<JournalEntry> findByPrivacy(String privacy);
}
