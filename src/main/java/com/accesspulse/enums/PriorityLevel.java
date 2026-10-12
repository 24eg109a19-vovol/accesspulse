package com.accesspulse.enums;

public enum PriorityLevel {
    LOW("Low", "badge-priority-low", "#10b981"),
    MEDIUM("Medium", "badge-priority-medium", "#f59e0b"),
    HIGH("High", "badge-priority-high", "#f97316"),
    CRITICAL("Critical", "badge-priority-critical", "#ef4444");

    private final String displayName;
    private final String badgeClass;
    private final String hexColor;

    PriorityLevel(String displayName, String badgeClass, String hexColor) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
        this.hexColor = hexColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public String getHexColor() {
        return hexColor;
    }
}
