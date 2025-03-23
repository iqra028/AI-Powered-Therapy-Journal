package org.example.seproject1;
import org.springframework.stereotype.Component;

@Component
public class ManualJournal extends Journal {

    @Override
    public String writeEntry(String entry) {
        // Just return the entry as is (manual mode)
        return entry;
    }

    @Override
    public String summarize(String entries) {
        // Simple summary (e.g., first few lines) or a placeholder
        String[] lines = entries.split("\n");
        if (lines.length > 3) {
            return String.join("\n", java.util.Arrays.copyOfRange(lines, 0, 3));
        }
        return entries;
    }
}