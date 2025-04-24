package org.example.seproject1;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "admin")
class Admin extends User {
    public Admin(String id, String username, String email, String password) {
        super(id, username, email, password, "Admin");
    }

    public void manageUsers() {
        System.out.println("Managing users...");
    }
}