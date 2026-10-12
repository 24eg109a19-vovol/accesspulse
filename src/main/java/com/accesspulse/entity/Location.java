package com.accesspulse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String buildingName;

    @Column(nullable = false, length = 80)
    private String floorOrLevel;

    @Column(nullable = false, length = 120)
    private String zoneOrRoom;

    @Column(length = 255)
    private String landmark;

    @Column(name = "importance_score")
    @Builder.Default
    private int importanceScore = 3; // 1 (low footfall) to 5 (critical main artery)

    private Double latitude;
    private Double longitude;

    @Column(length = 500)
    private String accessibilityFeatures;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public String getFormattedName() {
        return buildingName + " - " + floorOrLevel + " (" + zoneOrRoom + ")";
    }
}
