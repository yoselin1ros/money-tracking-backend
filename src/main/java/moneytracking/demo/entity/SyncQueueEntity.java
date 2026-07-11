package moneytracking.demo.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SyncQueueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Creates user_id foreign key column
    private UserEntity user;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "object_type", nullable = false)
    private String objectType;

    @Column(name = "object_id", nullable = false)
    private Integer objectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_ref_item_id")
    private RefItemEntity operation;

    @Column(name = "payload")
    private String payload;

    @Column(name = "client_timestamp")
    private Instant clientTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_ref_item_id")
    private RefItemEntity status;

    @Column(name = "retry_count")
    private Integer retry_Count;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public RefItemEntity getOperation() {
        return operation;
    }

    public void setOperation(RefItemEntity operation) {
        this.operation = operation;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getClientTimestamp() {
        return clientTimestamp;
    }

    public void setClientTimestamp(Instant clientTimestamp) {
        this.clientTimestamp = clientTimestamp;
    }

    public RefItemEntity getStatus() {
        return status;
    }

    public void setStatus(RefItemEntity status) {
        this.status = status;
    }

    public Integer getRetry_Count() {
        return retry_Count;
    }

    public void setRetry_Count(Integer retry_Count) {
        this.retry_Count = retry_Count;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "SyncQueueEntity [id=" + id + ", user=" + user + ", deviceId=" + deviceId + ", objectType=" + objectType
                + ", objectId=" + objectId + ", operation=" + operation + ", payload=" + payload + ", clientTimestamp="
                + clientTimestamp + ", status=" + status + ", retry_Count=" + retry_Count + ", createdAt=" + createdAt
                + "]";
    }

}
