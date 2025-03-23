package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3001", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        // Validate input
        if (username == null || email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username, email, and password are required"));
        }

        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already in use"));
        }

        // Create new user
        User user = new User(UUID.randomUUID().toString(), username, email, password, role != null ? role : "USER");

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        // Validate input
       // if (email == null || password == null) {
          //  return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        //}

        // Find user by email
        User user = userRepository.findByEmail(email).orElse(null);
        //if (user == null || !user.getPassword().equals(password)) {
          //  return ResponseEntity.badRequest().body(Map.of("error", "Invalid email or password"));
        //}

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }
}
