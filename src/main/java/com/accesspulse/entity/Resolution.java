package com.accesspulse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resolutions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "complaint_id", nullable = false, unique = true)
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id", nullable = false)
    private User resolvedBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String completionNotes;

    @Column(length = 255)
    private String resolutionImagePath;

    @Column(nullable = false, updatable = false)
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        this.resolvedAt = LocalDateTime.now();
    }
}
