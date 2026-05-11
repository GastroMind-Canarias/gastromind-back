package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities;

import com.gastromind.api.domain.models.enums.PendingStoreStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Tabla {@code pending_store}: cola de nombres de tienda detectados en tickets antes de promoción.
 */
@Entity
@Table(name = "pending_store")
public class PendingStoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "detected_name", nullable = false)
    private String detectedName;

    @Column(name = "detected_name_norm", nullable = false)
    private String detectedNameNorm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PendingStoreStatus status;

    @Column(name = "sightings_count", nullable = false)
    private int sightingsCount;

    @Column(name = "first_seen_at", nullable = false)
    private LocalDateTime firstSeenAt;

    @Column(name = "last_seen_at", nullable = false)
    private LocalDateTime lastSeenAt;

    @Column(name = "resolved_store_id")
    private String resolvedStoreId;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetectedName() {
        return detectedName;
    }

    public void setDetectedName(String detectedName) {
        this.detectedName = detectedName;
    }

    public String getDetectedNameNorm() {
        return detectedNameNorm;
    }

    public void setDetectedNameNorm(String detectedNameNorm) {
        this.detectedNameNorm = detectedNameNorm;
    }

    public PendingStoreStatus getStatus() {
        return status;
    }

    public void setStatus(PendingStoreStatus status) {
        this.status = status;
    }

    public int getSightingsCount() {
        return sightingsCount;
    }

    public void setSightingsCount(int sightingsCount) {
        this.sightingsCount = sightingsCount;
    }

    public LocalDateTime getFirstSeenAt() {
        return firstSeenAt;
    }

    public void setFirstSeenAt(LocalDateTime firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public String getResolvedStoreId() {
        return resolvedStoreId;
    }

    public void setResolvedStoreId(String resolvedStoreId) {
        this.resolvedStoreId = resolvedStoreId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
