package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class GeminiController {

    @Autowired
    private AIJournal aiJournal;

    @Autowired
    private ManualJournal manualJournal;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private JournalService journalService;

    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> getJournalResponse(@RequestBody Map<String, String> request, @RequestParam("mode") String mode) {
        String prompt = request.get("prompt");
        String result;
        if ("ai".equals(mode)) {
            result = aiJournal.writeEntry(prompt);
        } else {
            result = manualJournal.writeEntry(prompt);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/summarize")
    public ResponseEntity<Map<String, String>> summarizeChat(@RequestBody Map<String, String> request, @RequestParam("mode") String mode) {
        String chatHistory = request.get("prompt");
        String summary;
        if ("ai".equals(mode)) {
            summary = aiJournal.summarize(chatHistory);
        } else {
            summary = manualJournal.summarize(chatHistory);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", summary);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save-journal")
    public ResponseEntity<Map<String, Object>> saveJournal(@RequestBody Map<String, Object> request) {
        try {
            // Extract data from request
            String content = (String) request.get("content");
            String mood = (String) request.getOrDefault("mood", "Neutral");
            String type = (String) request.getOrDefault("type", "AI");
            String privacy = (String) request.getOrDefault("privacy", "private");

            // Detect tags if not provided
            List<String> tags;
            if (request.containsKey("tags")) {
                tags = (List<String>) request.get("tags");
            } else {
                tags = geminiService.extractTags(content);
            }

            // Create and populate journal entry
            JournalEntry entry = new JournalEntry();
            entry.setContent(content);
            entry.setMood(mood);
            entry.setType(type);
            entry.setPrivacy(privacy);
            entry.setDate(new Date());
            entry.setTime(new Date());
            entry.setCreatedAt(new Date());

            // Save to database
            JournalEntry savedEntry = journalService.saveJournalEntry(entry);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Journal entry saved successfully");
            response.put("id", savedEntry.getId());
            response.put("tags", tags);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to save journal: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/update-summary")
    public ResponseEntity<Map<String, Object>> updateSummary(@RequestBody Map<String, Object> request) {
        try {
            String summaryId = (String) request.get("id");
            String updatedContent = (String) request.get("content");

            // Get existing entry
            Optional<JournalEntry> optionalEntry = journalService.getJournalEntryById(summaryId);

            if (!optionalEntry.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Journal entry not found");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            JournalEntry entry = optionalEntry.get();
            entry.setContent(updatedContent);

            // Update entry
            JournalEntry updatedEntry = journalService.saveJournalEntry(entry);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Journal summary updated successfully");
            response.put("entry", updatedEntry);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update summary: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}