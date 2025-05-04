package org.example.seproject1;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@RequestMapping("/api/therapist")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TherapistService therapistService;
    @Autowired
    private FeedbackRepository feedbackRepository;

    @RequestMapping(value = "/admin/profiles/{profileId}", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }
    // Create or update profile with token-based authentication
    @PostMapping("/profile")
    public ResponseEntity<?> createProfile(@RequestBody ProfileDTO profileDTO, @RequestHeader("Authorization") String token) {
        try {
            // Extract therapist ID from token
            String therapistId = extractTherapistIdFromToken(token);

            // Check if therapist exists
            Optional<Therapist> therapistOptional = therapistService.getTherapistById(therapistId);

            if (!therapistOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Therapist not authenticated");
            }

            // Check if profile already exists
            Optional<Profile> existingProfile = profileRepository.findByTherapistId(therapistId);
            Profile profile;

            if (existingProfile.isPresent()) {
                profile = existingProfile.get();
                updateProfileFromDTO(profile, profileDTO);
            } else {
                profile = createProfileFromDTO(profileDTO);
                profile.setTherapistId(therapistId);
                profile.setApproved(false); // New profiles start as unapproved
                profile.setRating(0.0);
                profile.setReviewCount(0);
            }

            Profile savedProfile = profileRepository.save(profile);
            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating profile: " + e.getMessage());
        }
    }

    // Get therapist profile using token
    @GetMapping("/my-profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String therapistId = extractTherapistIdFromToken(token);
            Optional<Profile> profile = profileRepository.findByTherapistId(therapistId);

            if (profile.isPresent()) {
                return ResponseEntity.ok(profile.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving profile: " + e.getMessage());
        }
    }
    @GetMapping("/profile-by-id/{profileId}")
    public ResponseEntity<?> getProfileById(@PathVariable String profileId) {
        try {
            Optional<Profile> profile = profileRepository.findById(profileId);
            if (profile.isPresent()) {
                return ResponseEntity.ok(profile.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving profile: " + e.getMessage());
        }
    }

    // Admin endpoint to approve or reject profiles
    @PostMapping ("/admin/profiles/{profileId}")
    public ResponseEntity<?> updateApprovalStatus(
            @PathVariable String profileId,
            @RequestBody Map<String, Boolean> payload,
            @RequestHeader("Authorization") String token) {

        try {
            // Check if user is admin (implement your admin check logic here)
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
            }

            Optional<Profile> profileOptional = profileRepository.findById(profileId);
            if (!profileOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
            }

            Profile profile = profileOptional.get();
            profile.setApproved(payload.get("approved"));
            profileRepository.save(profile);

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating profile approval: " + e.getMessage());
        }
    }

    // Admin endpoint to get all pending profiles
    @GetMapping("/admin/profiles")
    public ResponseEntity<?> getPendingProfiles(
            @RequestParam(required = false) Boolean approved,
            @RequestHeader("Authorization") String token) {

        try {
            // Check if user is admin
            if (!isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
            }

            List<Profile> profiles;
            if (approved != null) {
                profiles = profileRepository.findByApproved(approved);
            } else {
                profiles = profileRepository.findAll();
            }

            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving profiles: " + e.getMessage());
        }
    }

    // Helper methods
    private Profile createProfileFromDTO(ProfileDTO dto) {
        Profile profile = new Profile();
        return updateProfileFromDTO(profile, dto);
    }

    private Profile updateProfileFromDTO(Profile profile, ProfileDTO dto) {
        profile.setFullName(dto.getFullName());
        profile.setSpecialty(dto.getSpecialty());
        profile.setBio(dto.getBio());
        profile.setEducation(dto.getEducation());
        profile.setSpecialties(dto.getSpecialties().toArray(new String[0]));
        profile.setInsuranceAccepted(dto.getInsuranceAccepted().toArray(new String[0]));
        return profile;
    }

    private boolean isAdmin(String token) {
        // Extract user type from token
        if (token == null || !token.contains("admin")) {
            return false;
        }
        return token.contains("admin-auth-");
    }

    // Helper method to extract therapist ID from token
    private String extractTherapistIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token.replace("therapist-auth-", "");
    }
    // Get therapist profile status (exists and approved)
    @GetMapping("/profile/status")
    public ResponseEntity<?> getProfileStatus(@RequestHeader("Authorization") String token) {
        try {
            String therapistId = extractTherapistIdFromToken(token);
            Optional<Profile> profileOptional = profileRepository.findByTherapistId(therapistId);

            Map<String, Object> status = new HashMap<>();
            status.put("exists", profileOptional.isPresent());
            status.put("approved", profileOptional.isPresent() && profileOptional.get().isApproved());

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking profile status: " + e.getMessage());
        }
    }
    // Add this to your ProfileController.java
    @GetMapping("/approved-profiles")
    public ResponseEntity<?> getApprovedProfiles() {
        try {
            List<Profile> approvedProfiles = profileRepository.findByApproved(true);
            return ResponseEntity.ok(approvedProfiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving approved profiles: " + e.getMessage());
        }
    }
    private void updateTherapistRating(String therapistId) {
        List<Feedback> feedbacks = feedbackRepository.findByTherapistId(therapistId);
        if (!feedbacks.isEmpty()) {
            double averageRating = feedbacks.stream()
                    .mapToInt(Feedback::getRating)
                    .average()
                    .orElse(0.0);
            long reviewCount = feedbackRepository.countByTherapistId(therapistId);

            // Update profile with new rating
            profileRepository.findByTherapistId(therapistId).ifPresent(profile -> {
                profile.setRating(averageRating);
                profile.setReviewCount(reviewCount);
                profileRepository.save(profile);
            });
        }
    }
    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(
            @RequestHeader("Authorization") String token,
            @RequestParam String therapistId,
            @RequestBody Feedback feedback,
            HttpServletRequest request) {

        try {
            // Extract user info
            String userId = extractUserIdFromToken(token);
            String userName = getUserNameFromToken(request);

            // Set user info in feedback
            feedback.setUserId(userId);
            feedback.setUserName(userName);
            feedback.setTherapistId(therapistId);
            feedback.setCreatedAt(new Date());

            // Save feedback
            Feedback savedFeedback = feedbackRepository.save(feedback);

            // Update therapist rating
            updateTherapistRating(therapistId);

            return ResponseEntity.ok(Map.of(
                    "message", "Feedback submitted successfully",
                    "feedbackId", savedFeedback.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error submitting feedback: " + e.getMessage());
        }
    }

    private String extractUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Assuming your token format is: "type-userId-randomString"
        // Example: "user-123-abc123def456"
        String[] parts = token.split("-");

        // Check if we have enough parts
        if (parts.length >= 2) {
            return parts[1]; // Return the userId part
        }

        return null;
    }

    private String getUserNameFromToken(HttpServletRequest request) {
        // Try to get username from header first
        String username = request.getHeader("X-Username");

        if (username != null && !username.isEmpty()) {
            return username;
        }

        // Fallback to extracting from token if needed
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // Assuming token contains username as last part: "type-userId-username"
            String[] parts = token.split("-");
            if (parts.length >= 3) {
                return parts[2];
            }
        }

        return "Anonymous";
    }
    @GetMapping("/feedback/stats")
    public ResponseEntity<?> getFeedbackStats(@RequestParam String therapistId) {
        try {
            List<Feedback> feedbacks = feedbackRepository.findByTherapistId(therapistId);

            Map<Integer, Integer> ratingCounts = new HashMap<>();
            // Initialize counts for all possible ratings
            for (int i = 1; i <= 5; i++) {
                ratingCounts.put(i, 0);
            }

            // Count each rating
            feedbacks.forEach(feedback -> {
                int rating = feedback.getRating();
                ratingCounts.put(rating, ratingCounts.get(rating) + 1);
            });

            return ResponseEntity.ok(Map.of(
                    "ratingCounts", ratingCounts,
                    "total", feedbacks.size(),
                    "average", feedbacks.stream().mapToInt(Feedback::getRating).average().orElse(0.0)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving feedback stats: " + e.getMessage());
        }
    }

}