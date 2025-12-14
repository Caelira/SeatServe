package org.example.angelsburgerpos.model;

public class User {
    private String username;
    private String password;
    private String role;
    private String fullname;

    public User(String username, String password, String role, String fullname) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getFullname() {
        return fullname;
    }
}