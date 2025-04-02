package org.example.seproject1;
class Admin extends User {
    public Admin(String id, String username, String email, String password) {
        super(id, username, email, password, "Admin");
    }

    public void manageUsers() {
        System.out.println("Managing users...");
    }
}