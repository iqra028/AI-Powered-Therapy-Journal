package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<JournalEntry> getAllJournalEntries() {
        return journalEntryRepository.findAll();
    }

    public void deleteJournalEntry(String id) {
        journalEntryRepository.deleteById(id);
    }
}
