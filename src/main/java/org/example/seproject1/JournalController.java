package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journals")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class JournalController {
    @Autowired
    private JournalService journalService;

    @PostMapping
    public JournalEntry createJournal(
            @RequestHeader("Authorization") String token,
            @RequestBody JournalEntry entry) {

        // Extract userId from token
        String userId = token.replace("Bearer ", "").replace("user-auth-", "");

        // Set userId and timestamps
        entry.setUserId(userId);
        entry.setCreatedAt(new Date());

        // If date/time not provided, set to current time
        if (entry.getDate() == null) {
            entry.setDate(new Date());
        }
        if (entry.getTime() == null) {
            entry.setTime(new Date());
        }

        return journalService.saveJournalEntry(entry);
    }

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

}