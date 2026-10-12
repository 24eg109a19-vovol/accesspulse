package com.accesspulse.enums;

public enum ComplaintCategory {
    // 12 Campus Categories requested
    ELEVATOR_ISSUES("Elevator / Lift Issues", "fa-solid fa-elevator", 5, 5),
    ELECTRICAL_ISSUES("Electrical Issues", "fa-solid fa-bolt", 4, 4),
    INFRASTRUCTURE_DAMAGE("Infrastructure Damage", "fa-solid fa-trowel-bricks", 4, 3),
    ACCESSIBILITY_PROBLEMS("Accessibility Problems", "fa-solid fa-wheelchair", 5, 4),
    WATER_PLUMBING("Water and Plumbing", "fa-solid fa-faucet-drip", 3, 3),
    RESTROOM_ISSUES("Restroom Issues", "fa-solid fa-restroom", 4, 3),
    CLASSROOM_PROBLEMS("Classroom Problems", "fa-solid fa-chalkboard-user", 3, 2),
    LABORATORY_PROBLEMS("Laboratory Problems", "fa-solid fa-flask-vial", 4, 3),
    SAFETY_ISSUES("Safety Issues", "fa-solid fa-shield-halved", 5, 5),
    NETWORK_PROBLEMS("Network & Wi-Fi Problems", "fa-solid fa-wifi", 3, 1),
    CLEANLINESS_ISSUES("Cleanliness Issues", "fa-solid fa-broom", 3, 2),
    HOSTEL_ISSUES("Hostel Issues", "fa-solid fa-bed", 4, 3),

    // Legacy accessibility aliases (for backward compatibility)
    BROKEN_ELEVATOR("Broken Elevator / Lift", "fa-solid fa-elevator", 5, 5),
    BLOCKED_RAMP("Blocked or Damaged Ramp", "fa-solid fa-wheelchair", 4, 4),
    INACCESSIBLE_WASHROOM("Inaccessible Washroom", "fa-solid fa-restroom", 4, 4),
    DAMAGED_HANDRAIL("Damaged Handrail / Support", "fa-solid fa-hand-holding", 3, 3),
    PATHWAY_OBSTRUCTION("Pathway Obstruction", "fa-solid fa-road-barrier", 3, 3),
    OTHER("Other Campus Issue", "fa-solid fa-triangle-exclamation", 2, 2);

    private final String displayName;
    private final String iconClass;
    private final int defaultImpact;
    private final int defaultSafetyRisk;

    ComplaintCategory(String displayName, String iconClass, int defaultImpact, int defaultSafetyRisk) {
        this.displayName = displayName;
        this.iconClass = iconClass;
        this.defaultImpact = defaultImpact;
        this.defaultSafetyRisk = defaultSafetyRisk;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconClass() {
        return iconClass;
    }

    public int getDefaultImpact() {
        return defaultImpact;
    }

    public int getDefaultSafetyRisk() {
        return defaultSafetyRisk;
    }
}
