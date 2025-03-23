package org.example.seproject1;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final List<User> users = new ArrayList<>();

    public Optional<User> findByEmail(String email) {
        return users.stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }

    public Optional<User> findByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    public List<User> findByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals(role)) {
                result.add(user);
            }
        }
        return result;
    }

    public void save(User user) {
        users.add(user);
    }

    public void update(User updatedUser) {
        findByEmail(updatedUser.getEmail()).ifPresent(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setRole(updatedUser.getRole());
        });
    }

    public void delete(String email) {
        users.removeIf(user -> user.getEmail().equals(email));
    }
}
