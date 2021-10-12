package com.jexige.hotelsapi.model.admin;

public enum UserRole {
    ADMIN("ROLE_admin"),
    USER("ROLE_user");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
