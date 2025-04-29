package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/therapist")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TherapistService therapistService;

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

    // Admin endpoint to approve or reject profiles
    @PatchMapping("/admin/profiles/{profileId}")
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

}