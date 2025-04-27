package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private TherapistService therapistService;

    // Get all users (accessible only by admins)
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        // Verify admin token
        if (!isValidAdminToken(token)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get all therapists (accessible only by admins)
    @GetMapping("/therapists")
    public ResponseEntity<?> getAllTherapists(@RequestHeader("Authorization") String token) {
        // Verify admin token
        if (!isValidAdminToken(token)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }

        List<Therapist> therapists = therapistService.getAllTherapists();
        return ResponseEntity.ok(therapists);
    }

    // Get all admins (accessible only by admins)
    @GetMapping("/admins")
    public ResponseEntity<?> getAllAdmins(@RequestHeader("Authorization") String token) {
        // Verify admin token
        if (!isValidAdminToken(token)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }

        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    // Delete a user (accessible only by admins)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id, @RequestHeader("Authorization") String token) {
        // Verify admin token
        if (!isValidAdminToken(token)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }

        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // Delete a therapist (accessible only by admins)
    @DeleteMapping("/therapists/{id}")
    public ResponseEntity<?> deleteTherapist(@PathVariable String id, @RequestHeader("Authorization") String token) {
        // Verify admin token
        if (!isValidAdminToken(token)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }

        therapistService.deleteTherapist(id);
        return ResponseEntity.ok(Map.of("message", "Therapist deleted successfully"));
    }

    // Helper method to validate admin token
    private boolean isValidAdminToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }

        String adminId = token.replace("Bearer ", "").replace("admin-auth-", "");
        Optional<Admin> admin = adminService.getAdminById(adminId);
        return admin.isPresent();
    }
    // Add these methods to AdminController
    @GetMapping("/pending-profiles")
    public ResponseEntity<?> getPendingProfiles(@RequestHeader("Authorization") String token) {
        if (!isValidAdminToken(token)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }
        List<Profile> profiles = therapistService.getPendingProfiles();
        return ResponseEntity.ok(profiles);
    }

    @PostMapping("/approve-profile/{profileId}")
    public ResponseEntity<?> approveProfile(
            @RequestHeader("Authorization") String token,
            @PathVariable String profileId) {
        if (!isValidAdminToken(token)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }
        Profile approvedProfile = therapistService.approveProfile(profileId);
        return ResponseEntity.ok(approvedProfile);
    }
}
