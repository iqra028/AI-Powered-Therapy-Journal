package org.example.seproject1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Predefined moods
    private final List<String> MOODS = Arrays.asList(
            "Happy", "Sad", "Angry", "Anxious", "Excited", "Tired", "Neutral"
    );

    // Predefined tags
    private final List<String> COMMON_TAGS = Arrays.asList(
            "Work", "Family", "Health", "Relationships", "Personal", "Goals",
            "Achievements", "Challenges", "Travel", "Hobbies", "Education"
    );

    public String generateResponse(String prompt) {
        System.out.println("Generating response for prompt: " + prompt); // Debugging
        String refinedPrompt = prompt + "\nAnswer in a short paragraph, no more than 3-4 sentences. Avoid giving suggestions or advice, just respond conversationally.";
        return callGeminiAPI(refinedPrompt);
    }

    public String summarizeChat(String chatHistory) {
        System.out.println("Summarizing chat history: " + chatHistory); // Debugging
        return callGeminiAPI("Summarize the following journal entry as if it were written by the user. Focus on the user's feelings, experiences, and key events. Do not mention the AI or any external entities. Keep the summary concise and in the first person.\n" + chatHistory);
    }

    public String detectMood(String text) {
        System.out.println("Detecting mood for text: " + text); // Debugging

        // Create a specific prompt for Gemini to determine the mood
        String prompt = "Analyze the following journal entry and determine the primary mood expressed. " +
                "Choose exactly one mood from this list: " + String.join(", ", MOODS) + ". " +
                "Return only the mood name, nothing else.\n\nJournal entry: " + text;

        String response = callGeminiAPI(prompt);
        System.out.println("Gemini mood response: " + response); // Debugging

        // Process the response to extract just the mood
        String detectedMood = "Neutral"; // Default mood

        // Clean up the response
        String cleanResponse = response.trim();

        // Check for exact mood names in the response
        for (String mood : MOODS) {
            if (cleanResponse.equalsIgnoreCase(mood) ||
                    cleanResponse.contains(mood) ||
                    response.contains(mood)) {
                detectedMood = mood;
                break;
            }
        }

        System.out.println("Final detected mood: " + detectedMood); // Debugging
        return detectedMood;
    }

    public List<String> extractTags(String text) {
        System.out.println("Extracting tags for text: " + text); // Debugging

        // Create a specific prompt for Gemini to extract the most relevant tags
        String prompt = "Analyze the following journal entry and select exactly 3 tags that best represent its content. " +
                "Choose only from this list: " + String.join(", ", COMMON_TAGS) + ". " +
                "Return only the tag names separated by commas, with no additional text or explanation.\n\nJournal entry: " + text;

        String response = callGeminiAPI(prompt);
        System.out.println("Gemini tag response: " + response); // Debugging

        List<String> extractedTags = new ArrayList<>();

        // Process the response - splitting by commas and trimming whitespace
        String[] tagCandidates = response.split(",");
        for (String tag : tagCandidates) {
            String trimmedTag = tag.trim();
            // Verify the tag is in our allowed list
            if (COMMON_TAGS.contains(trimmedTag) && !extractedTags.contains(trimmedTag) && extractedTags.size() < 3) {
                extractedTags.add(trimmedTag);
            }
        }

        // Fallback if we didn't get valid tags
        if (extractedTags.isEmpty()) {
            // Extract tags using regex as a backup method
            Pattern pattern = Pattern.compile("\\b(" + String.join("|", COMMON_TAGS) + ")\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(response);

            while (matcher.find() && extractedTags.size() < 3) {
                String tag = matcher.group(1);
                // Find the exact case match in our COMMON_TAGS list
                for (String commonTag : COMMON_TAGS) {
                    if (commonTag.equalsIgnoreCase(tag) && !extractedTags.contains(commonTag)) {
                        extractedTags.add(commonTag);
                        break;
                    }
                }
            }
        }

        // If still empty, add a default tag
        if (extractedTags.isEmpty()) {
            extractedTags.add("Personal");
        }

        System.out.println("Final extracted tags: " + extractedTags); // Debugging
        return extractedTags;
    }

    private String callGeminiAPI(String prompt) {
        try {
            System.out.println("Calling Gemini API with prompt: " + prompt); // Debugging

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String urlWithKey = apiUrl + "?key=" + apiKey;
            System.out.println("API URL: " + urlWithKey); // Debugging
            System.out.println("Request headers: " + headers); // Debugging
            System.out.println("Request body: " + requestBody); // Debugging

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    urlWithKey,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            System.out.println("API response status: " + response.getStatusCode()); // Debugging
            System.out.println("API response body: " + response.getBody()); // Debugging

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            JsonNode candidates = jsonResponse.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode contentParts = firstCandidate.path("content").path("parts");

                if (contentParts.isArray() && contentParts.size() > 0) {
                    String responseText = contentParts.get(0).path("text").asText();
                    System.out.println("API response text: " + responseText); // Debugging
                    return responseText;
                }
            }

            System.out.println("No valid response from Gemini API."); // Debugging
            return "No response from Gemini AI.";

        } catch (Exception e) {
            System.err.println("Error while calling Gemini API: " + e.getMessage()); // Debugging
            e.printStackTrace(); // Print the full stack trace for debugging
            return "No response from Gemini AI.";
        }
    }
}