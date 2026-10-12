package com.accesspulse.enums;

public enum ComplaintCategory {
    BROKEN_ELEVATOR("Broken Elevator / Lift", "fa-solid fa-elevator", 5, 5),
    BLOCKED_RAMP("Blocked or Damaged Ramp", "fa-solid fa-wheelchair", 4, 4),
    INACCESSIBLE_WASHROOM("Inaccessible Washroom", "fa-solid fa-restroom", 4, 4),
    DAMAGED_HANDRAIL("Damaged Handrail / Support", "fa-solid fa-hand-holding", 3, 3),
    PATHWAY_OBSTRUCTION("Pathway / Sidewalk Obstruction", "fa-solid fa-road-barrier", 3, 3),
    DOOR_AUTOMATION_FAILURE("Automated Door Failure", "fa-solid fa-door-open", 3, 3),
    TACTILE_PAVING_DAMAGE("Missing / Damaged Tactile Paving", "fa-solid fa-blind", 4, 3),
    AUDITORY_SIGNAL_DEFECT("Defective Crosswalk Audio Signal", "fa-solid fa-volume-xmark", 4, 3),
    PARKING_SPACE_VIOLATION("Accessible Parking Blocked", "fa-solid fa-square-parking", 3, 3),
    OTHER("Other Accessibility Issue", "fa-solid fa-triangle-exclamation", 2, 2);

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
