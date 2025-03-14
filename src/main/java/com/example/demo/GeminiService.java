package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
@Service
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateResponse(String prompt) {
        String refinedPrompt = prompt + "\nAnswer in a short paragraph, no more than 3-4 sentences. Avoid giving suggestions or advice, just respond conversationally.";
        return callGeminiAPI(refinedPrompt);
    }

    public String summarizeChat(String chatHistory) {
        return callGeminiAPI("Summarize the following journal entry as if it were written by the user. Focus on the user's feelings, experiences, and key events. Do not mention the AI or any external entities. Keep the summary concise and in the first person.\n" + chatHistory);
    }

    private String callGeminiAPI(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String urlWithKey = apiUrl + "?key=" + apiKey;
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    urlWithKey,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            JsonNode candidates = jsonResponse.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode contentParts = firstCandidate.path("content").path("parts");

                if (contentParts.isArray() && contentParts.size() > 0) {
                    return contentParts.get(0).path("text").asText();
                }
            }

        } catch (Exception e) {
            System.err.println("Error while calling Gemini API: " + e.getMessage());
        }

        return "No response from Gemini AI.";
    }
}
