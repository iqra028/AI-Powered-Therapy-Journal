package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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

    public List<JournalEntry> getPublicJournals() {
        // Only return journals that are both public AND published
        return journalEntryRepository.findByPrivacyAndStatus("public", "published");
    }

    // FIXED: Changed to return both "pending" AND "draft" status journals
    public List<JournalEntry> getPendingJournals() {
        // Return all journals that need approval (either "pending" or "draft" status)
        return journalEntryRepository.findByStatusIn(List.of("pending", "draft"));
    }

    public JournalEntry likeJournal(String journalId, String userId) {
        JournalEntry journal = journalEntryRepository.findById(journalId).orElseThrow();

        if (journal.getLikes().contains(userId)) {
            // Unlike
            journal.getLikes().remove(userId);
            journal.setLikeCount(journal.getLikeCount() - 1);
        } else {
            // Like
            journal.getLikes().add(userId);
            journal.setLikeCount(journal.getLikeCount() + 1);
        }

        return journalEntryRepository.save(journal);
    }

    public JournalEntry changePrivacy(String journalId, String privacy, String userId) {
        JournalEntry journal = journalEntryRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Journal not found"));

        if (!journal.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        journal.setPrivacy(privacy);

        // Modified status logic
        if ("public".equals(privacy)) {
            journal.setStatus("pending"); // Set to pending when making public
        } else {
            journal.setStatus("draft");
        }

        return journalEntryRepository.save(journal);
    }

    // FIXED: Changed to accept both "pending" and "draft" status
    public JournalEntry approveJournal(String journalId) {
        JournalEntry journal = journalEntryRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Journal not found"));

        // Accept both "pending" and "draft" status journals
        if (!("pending".equals(journal.getStatus()) || "draft".equals(journal.getStatus()))) {
            throw new RuntimeException("Journal is not in a status that can be approved");
        }

        // Set to published and ensure it's public
        journal.setStatus("published");
        journal.setPrivacy("public");
        journal.setPublishedAt(new Date());
        return journalEntryRepository.save(journal);
    }

    // FIXED: Changed to accept both "pending" and "draft" status
    public JournalEntry rejectJournal(String journalId) {
        JournalEntry journal = journalEntryRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Journal not found"));

        // Accept both "pending" and "draft" status journals
        if (!("pending".equals(journal.getStatus()) || "draft".equals(journal.getStatus()))) {
            throw new RuntimeException("Journal is not in a status that can be rejected");
        }

        // Rejected journals become private
        journal.setStatus("rejected");
        journal.setPrivacy("private");
        return journalEntryRepository.save(journal);
    }
    public List<JournalEntry> getEntriesSince(String userId, Date date) {
        return journalEntryRepository.findByUserIdAndDateAfter(userId, date);
    }
}