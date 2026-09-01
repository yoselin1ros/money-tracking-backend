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
import jakarta.persistence.Table;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "password_reset_tokens", schema = "migrations")
public class PasswordResetTokenEntity {
    private static final int EXPIRATION_MINUTES = 60; // Token expires in 60 minutes

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

    public PasswordResetTokenEntity() {
        // Default constructor for JPA
    }

    public PasswordResetTokenEntity(String tokenHash, UserEntity user) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiresAt = Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);
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
        return "PasswordResetTokenEntity [id=" + id + ", user=" + user + ", tokenHash=" + tokenHash + ", expiresAt="
                + expiresAt + ", usedAt=" + usedAt + ", createdAt=" + createdAt + "]";
    }  
    
}
