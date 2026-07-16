package moneytracking.demo.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions", schema = "migrations")
public class SessionEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    // @Column(name = "user_id", nullable = false)
    // private Integer userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Creates user_id foreign key column
    private UserEntity user;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "device_name", nullable = false)
    private String device_name;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    private boolean revoked;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

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

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(Instant lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    @Override
    public String toString() {
        return "SessionEntity [id=" + id + ", user=" + user + ", tokenHash=" + tokenHash + ", deviceId=" + deviceId
                + ", device_name=" + device_name + ", expiresAt=" + expiresAt + ", createdAt=" + createdAt
                + ", revoked=" + revoked + ", lastActivityAt=" + lastActivityAt + "]";
    }

    @PrePersist
    protected void calculateExpiry() {
        // Automatically sets expiration to 30 days after the current time
        this.expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
    }

}
