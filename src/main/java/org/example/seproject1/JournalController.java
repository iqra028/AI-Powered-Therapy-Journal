package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journals")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class JournalController {
    @Autowired
    private JournalService journalService;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/{id}")
    public ResponseEntity<JournalEntry> getJournalById(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        String userId = extractUserIdFromToken(token);
        Optional<JournalEntry> journal = journalService.getJournalEntryById(id);

        if (journal.isPresent() && journal.get().getUserId().equals(userId)) {
            return ResponseEntity.ok(journal.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<JournalEntry> getAllJournals(
            @RequestHeader("Authorization") String token) {
        String userId = extractUserIdFromToken(token);
        return journalService.getAllJournalEntriesForUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJournal(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        String userId = extractUserIdFromToken(token);
        Optional<JournalEntry> journal = journalService.getJournalEntryById(id);

        if (journal.isPresent() && journal.get().getUserId().equals(userId)) {
            journalService.deleteJournalEntry(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Helper method to extract userId from token
    private String extractUserIdFromToken(String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // Remove "user-auth-" prefix
        return token.replace("user-auth-", "");
    }
    @PutMapping("/{id}")
    public ResponseEntity<JournalEntry> updateJournal(
            @RequestHeader("Authorization") String token,
            @PathVariable String id,
            @RequestBody JournalEntry updatedEntry) {

        String userId = extractUserIdFromToken(token);
        Optional<JournalEntry> existingEntryOpt = journalService.getJournalEntryById(id);

        if (existingEntryOpt.isPresent() && existingEntryOpt.get().getUserId().equals(userId)) {
            JournalEntry existingEntry = existingEntryOpt.get();

            // Update fields
            existingEntry.setContent(updatedEntry.getContent());
            existingEntry.setType(updatedEntry.getType());
            existingEntry.setMood(updatedEntry.getMood());
            existingEntry.setImageUrl(updatedEntry.getImageUrl());
            existingEntry.setDate(updatedEntry.getDate());
            existingEntry.setTime(updatedEntry.getTime());
            existingEntry.setPrivacy(updatedEntry.getPrivacy());
            existingEntry.setTags(updatedEntry.getTags());

            JournalEntry savedEntry = journalService.saveJournalEntry(existingEntry);
            return ResponseEntity.ok(savedEntry);
        }

        return ResponseEntity.notFound().build();
    }
    @GetMapping("/public")
    public List<JournalEntry> getPublicJournals() {
        return journalService.getPublicJournals();
    }

    @PostMapping("/{id}/like")
    public JournalEntry likeJournal(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        String userId = extractUserIdFromToken(token);
        return journalService.likeJournal(id, userId);
    }

    @PutMapping("/{id}/privacy")
    public JournalEntry changePrivacy(
            @RequestHeader("Authorization") String token,
            @PathVariable String id,
            @RequestParam String privacy) {
        String userId = extractUserIdFromToken(token);
        JournalEntry updatedEntry = journalService.changePrivacy(id, privacy, userId);

        // Add notification logic if needed
        return updatedEntry;
    }
    @PutMapping("/{id}/approve")
    public JournalEntry approveJournal(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        // Add admin check here
        return journalService.approveJournal(id);
    }

    @PutMapping("/{id}/reject")
    public JournalEntry rejectJournal(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        // Add admin check here
        return journalService.rejectJournal(id);
    }

    @PostMapping
    public JournalEntry createJournal(
            @RequestHeader("Authorization") String token,
            @RequestBody JournalEntry entry) {

        String userId = extractUserIdFromToken(token);
        entry.setUserId(userId);
        entry.setAuthorName("User " + userId.substring(0, 4)); // Default name - replace with actual user name
        entry.setCreatedAt(new Date());

        if (entry.getDate() == null) entry.setDate(new Date());
        if (entry.getTime() == null) entry.setTime(new Date());
        if (entry.getPrivacy() == null) entry.setPrivacy("private");

        // Modified logic for status
        if ("public".equals(entry.getPrivacy())) {
            entry.setStatus("pending"); // Set to pending instead of public
        } else {
            entry.setStatus("draft");
        }

        return journalService.saveJournalEntry(entry);
    }
    // Add to JournalController.java

    @GetMapping("/api/admin/pending-journals")
    public ResponseEntity<List<JournalEntry>> getPendingJournalsAdmin(
            @RequestHeader("Authorization") String token) {
        // Optional: Add admin verification logic here
        String userId = extractUserIdFromToken(token);
        // In a real app, you would check if this userId has admin role

        return ResponseEntity.ok(journalService.getPendingJournals());
    }

    @PutMapping("/api/admin/approve-journal/{id}")
    public ResponseEntity<JournalEntry> approveJournalAdmin(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        // Optional: Add admin verification
        String userId = extractUserIdFromToken(token);
        // In a real app, you would check if this userId has admin role

        return ResponseEntity.ok(journalService.approveJournal(id));
    }

    @PutMapping("/api/admin/reject-journal/{id}")
    public ResponseEntity<JournalEntry> rejectJournalAdmin(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        // Optional: Add admin verification
        String userId = extractUserIdFromToken(token);
        // In a real app, you would check if this userId has admin role

        return ResponseEntity.ok(journalService.rejectJournal(id));
    }
    @GetMapping("/pending")
    public List<JournalEntry> getPendingJournals(@RequestHeader("Authorization") String token) {
        return journalService.getPendingJournalsWithUsernames();
    }


}