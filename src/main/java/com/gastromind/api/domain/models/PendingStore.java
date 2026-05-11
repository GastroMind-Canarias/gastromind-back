package com.gastromind.api.domain.models;

import com.gastromind.api.domain.models.enums.PendingStoreStatus;

import java.time.LocalDateTime;

/**
 * Tienda vista en tickets u orígenes externos aún no fusionada al catálogo: acumula avistamientos hasta promoción o rechazo manual.
 */
public class PendingStore {
    private String id;
    private String detectedName;
    private String detectedNameNorm;
    private PendingStoreStatus status;
    private int sightingsCount;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;
    private String resolvedStoreId;
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
