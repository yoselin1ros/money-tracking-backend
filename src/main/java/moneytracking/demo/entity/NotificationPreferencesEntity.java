package moneytracking.demo.entity;

import java.time.Instant;

import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class NotificationPreferencesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Creates user_id foreign key column
    private UserEntity user;

    @Column(name = "budget_approaching_enabled")
    private boolean budgetApproachingEnabled = false;

    @Column(name = "budget_exceeded_enabled")
    private boolean budgetExceededEnabled = false;

    @Column(name = "sync_conflict_enabled")
    private boolean syncConflictEnabled = false;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

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

    public boolean isBudgetApproachingEnabled() {
        return budgetApproachingEnabled;
    }

    public void setBudgetApproachingEnabled(boolean budgetApproachingEnabled) {
        this.budgetApproachingEnabled = budgetApproachingEnabled;
    }

    public boolean isBudgetExceededEnabled() {
        return budgetExceededEnabled;
    }

    public void setBudgetExceededEnabled(boolean budgetExceededEnabled) {
        this.budgetExceededEnabled = budgetExceededEnabled;
    }

    public boolean isSyncConflictEnabled() {
        return syncConflictEnabled;
    }

    public void setSyncConflictEnabled(boolean syncConflictEnabled) {
        this.syncConflictEnabled = syncConflictEnabled;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "NotificationPreferencesEntity [id=" + id + ", user=" + user + ", budgetApproachingEnabled="
                + budgetApproachingEnabled + ", budgetExceededEnabled=" + budgetExceededEnabled
                + ", syncConflictEnabled=" + syncConflictEnabled + ", updatedAt=" + updatedAt + "]";
    }

}
