package com.hiddless.java_fx.dto;

public enum ERole {
    STUDENT("Student"),
    TEACHER("Teacher"),
    ADMIN("admin");

    private final String description;
    ERole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ERole fromString(String role) {
        try {
            return ERole.valueOf(role.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Role: " + role);
        }
    }
}
