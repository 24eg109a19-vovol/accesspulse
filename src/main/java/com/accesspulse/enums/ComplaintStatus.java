package com.accesspulse.enums;

public enum ComplaintStatus {
    SUBMITTED("Submitted", "badge-subtle-primary"),
    UNDER_REVIEW("Under Review", "badge-subtle-info"),
    VERIFIED("Verified", "badge-subtle-secondary"),
    ASSIGNED("Assigned", "badge-subtle-warning"),
    IN_PROGRESS("In Progress", "badge-subtle-purple"),
    RESOLVED("Resolved", "badge-subtle-success"),
    CLOSED("Closed / Verified", "badge-subtle-teal"),
    REJECTED("Rejected", "badge-subtle-danger"),
    CANCELLED("Cancelled", "badge-subtle-dark");

    private final String displayName;
    private final String badgeClass;

    ComplaintStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
