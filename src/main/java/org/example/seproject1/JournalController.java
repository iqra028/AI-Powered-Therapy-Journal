package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journals")
public class JournalController {

    @Autowired
    private JournalService journalService;

    @PostMapping
    public JournalEntry createJournal(@RequestBody JournalEntry entry) {
        return journalService.saveJournalEntry(entry);
    }

    @GetMapping("/{id}")
    public Optional<JournalEntry> getJournalById(@PathVariable String id) {
        return journalService.getJournalEntryById(id);
    }

    @GetMapping
    public List<JournalEntry> getAllJournals() {
        return journalService.getAllJournalEntries();
    }

    @DeleteMapping("/{id}")
    public void deleteJournal(@PathVariable String id) {
        journalService.deleteJournalEntry(id);
    }
}
