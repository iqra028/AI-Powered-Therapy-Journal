package org.example.seproject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    // Register a new admin
    public Admin registerAdmin(Admin admin) {
        // Generate a unique ID if not provided
        if (admin.getId() == null || admin.getId().isEmpty()) {
            admin.setId(UUID.randomUUID().toString());
        }
        return adminRepository.save(admin);
    }

    // Find an admin by email
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    // Find an admin by username
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    // Get an admin by ID
    public Optional<Admin> getAdminById(String id) {
        return adminRepository.findById(id);
    }

    // Get all admins
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Update an admin
    public Admin updateAdmin(String id, Admin updatedAdmin) {
        return adminRepository.findById(id)
                .map(admin -> {
                    admin.setUsername(updatedAdmin.getUsername());
                    admin.setEmail(updatedAdmin.getEmail());
                    admin.setPassword(updatedAdmin.getPassword());
                    return adminRepository.save(admin);
                })
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
    }

    // Delete an admin
    public void deleteAdmin(String id) {
        adminRepository.deleteById(id);
    }
}
