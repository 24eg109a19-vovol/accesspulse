package com.accesspulse.enums;

public enum Role {
    ROLE_STUDENT("Student"),
    ROLE_STAFF("Staff Member"),
    ROLE_ADMIN("Administrator"),
    ROLE_MAINTENANCE("Maintenance Staff");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
