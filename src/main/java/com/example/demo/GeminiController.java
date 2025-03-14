package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class GeminiController {
    @Autowired
    private GeminiService geminiService;

    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> getGeminiResponse(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String result = geminiService.generateResponse(prompt);

        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/summarize")
    public ResponseEntity<Map<String, String>> summarizeChat(@RequestBody Map<String, String> request) {
        String chatHistory = request.get("prompt");
        String summary = geminiService.summarizeChat(chatHistory);

        Map<String, String> response = new HashMap<>();
        response.put("message", summary);
        return ResponseEntity.ok(response);
    }
}
