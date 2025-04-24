package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TherapistService therapistService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        // Check if user already exists
        Optional<User> existingUserByEmail = userService.findByEmail(registrationRequest.getEmail());
        if (existingUserByEmail.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }

        // Check if admin or therapist with same email exists
        if (adminService.findByEmail(registrationRequest.getEmail()).isPresent() ||
                therapistService.findByEmail(registrationRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }

        String role = registrationRequest.getRole();
        Map<String, Object> response = new HashMap<>();
        String token = "";

        if ("admin".equalsIgnoreCase(role))  {
            // Create new admin
            Admin newAdmin = new Admin(
                    UUID.randomUUID().toString(),
                    registrationRequest.getUsername(),
                    registrationRequest.getEmail(),
                    registrationRequest.getPassword()
            );

            // Save the admin
            Admin savedAdmin = adminService.registerAdmin(newAdmin);

            // Create response
            response.put("id", savedAdmin.getId());
            response.put("username", savedAdmin.getUsername());
            response.put("email", savedAdmin.getEmail());
            response.put("role", savedAdmin.getRole());
            token = "admin-auth-" + savedAdmin.getId();
        }
        else if ("therapist".equalsIgnoreCase(role)) {
            // Create new therapist
            Therapist newTherapist = new Therapist(
                    UUID.randomUUID().toString(),
                    registrationRequest.getUsername(),
                    registrationRequest.getEmail(),
                    registrationRequest.getPassword()
            );

            // Save the therapist
            Therapist savedTherapist = therapistService.registerTherapist(newTherapist);

            // Create response
            response.put("id", savedTherapist.getId());
            response.put("username", savedTherapist.getUsername());
            response.put("email", savedTherapist.getEmail());
            response.put("role", savedTherapist.getRole());
            token = "therapist-auth-" + savedTherapist.getId();
        }
        else {
            // Create new general user
            User newUser = new User();
            newUser.setUsername(registrationRequest.getUsername());
            newUser.setEmail(registrationRequest.getEmail());
            newUser.setPassword(registrationRequest.getPassword());
            newUser.setRole(registrationRequest.getRole());

            // Save the user
            User savedUser = userService.registerUser(newUser);

            // Create response
            response.put("id", savedUser.getId());
            response.put("username", savedUser.getUsername());
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole());
            token = "user-auth-" + savedUser.getId();
        }

        // Add token to response
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // Try to find user in each repository
        Optional<User> userOptional = userService.findByEmail(email);
        Optional<Admin> adminOptional = adminService.findByEmail(email);
        Optional<Therapist> therapistOptional = therapistService.findByEmail(email);

        Map<String, Object> response = new HashMap<>();

        // Check if user exists and password matches
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (password.equals(user.getPassword())) {
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("role", user.getRole());
                response.put("token", "user-auth-" + user.getId());
                return ResponseEntity.ok(response);
            }
        }

        // Check if admin exists and password matches
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            if (password.equals(admin.getPassword())) {
                response.put("id", admin.getId());
                response.put("username", admin.getUsername());
                response.put("email", admin.getEmail());
                response.put("role", admin.getRole());
                response.put("token", "admin-auth-" + admin.getId());
                return ResponseEntity.ok(response);
            }
        }

        // Check if therapist exists and password matches
        if (therapistOptional.isPresent()) {
            Therapist therapist = therapistOptional.get();
            if (password.equals(therapist.getPassword())) {
                response.put("id", therapist.getId());
                response.put("username", therapist.getUsername());
                response.put("email", therapist.getEmail());
                response.put("role", therapist.getRole());
                response.put("token", "therapist-auth-" + therapist.getId());
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
    }
}
