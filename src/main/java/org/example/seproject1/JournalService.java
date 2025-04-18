package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        maxAge = 3600)


@Service
public class JournalService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    public JournalEntry saveJournalEntry(JournalEntry entry) {
        return journalEntryRepository.save(entry);
    }

    public Optional<JournalEntry> getJournalEntryById(String id) {
        return journalEntryRepository.findById(id);
    }

    public List<JournalEntry> getAllJournalEntriesForUser(String userId) {
        return journalEntryRepository.findByUserId(userId);
    }

    public void deleteJournalEntry(String id) {
        journalEntryRepository.deleteById(id);
    }

    // Additional methods
    public List<JournalEntry> getJournalsByType(String userId, String type) {
        return journalEntryRepository.findByUserIdAndType(userId, type);
    }

    public List<JournalEntry> getJournalsByMood(String userId, String mood) {
        return journalEntryRepository.findByUserIdAndMood(userId, mood);
    }
}