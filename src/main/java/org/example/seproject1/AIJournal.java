package org.example.seproject1;

import org.example.seproject1.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AIJournal extends Journal {

    @Autowired
    private GeminiService geminiService;

    @Override
    public String writeEntry(String entry) {
        return geminiService.generateResponse(entry);
    }

    @Override
    public String summarize(String entries) {
        return geminiService.summarizeChat(entries);
    }
}