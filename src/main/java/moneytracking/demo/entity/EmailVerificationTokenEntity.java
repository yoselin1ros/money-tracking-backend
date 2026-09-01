package moneytracking.demo.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "email_verification_tokens", schema = "migrations")
public class EmailVerificationTokenEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Creates user_id foreign key column
    private UserEntity user;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
  
    @Column(name = "used_at")
    private Instant usedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public EmailVerificationTokenEntity() {
        // Default constructor for JPA
    }

    public EmailVerificationTokenEntity(String tokenHash, UserEntity user) {
        this.tokenHash = tokenHash;
        this.user = user;
    }

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

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Instant usedAt) {
        this.usedAt = usedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "EmailVerificationTokenEntity [id=" + id + ", user=" + user + ", tokenHash=" + tokenHash + ", expiresAt="
                + expiresAt + ", usedAt=" + usedAt + ", createdAt=" + createdAt + "]";
    }
    
    @PrePersist
    protected void calculateExpiry() {
        // Automatically sets expiration to 1 day after the current time
        this.expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);
    }
    
}
