package moneytracking.demo.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "history_logs", schema = "migrations")
public class HistoryLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Creates user_id foreign key column
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_type_ref_item_id")
    private RefItemEntity objectType;

    @Column(name = "object_id", nullable = false)
    private Integer object_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_ref_item_id")
    private RefItemEntity action;

    @Column(name = "previous_value")
    private String previousValue;

    @Column(name = "new_value")
    private String newValue;

    @Column(name = "occurred_at")
    private Instant occurredAt;

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

    public RefItemEntity getObjectType() {
        return objectType;
    }

    public void setObjectType(RefItemEntity objectType) {
        this.objectType = objectType;
    }

    public Integer getObject_id() {
        return object_id;
    }

    public void setObject_id(Integer object_id) {
        this.object_id = object_id;
    }

    public RefItemEntity getAction() {
        return action;
    }

    public void setAction(RefItemEntity action) {
        this.action = action;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    @Override
    public String toString() {
        return "HistoryLogEntity [id=" + id + ", user=" + user + ", objectType=" + objectType + ", object_id="
                + object_id + ", action=" + action + ", previousValue=" + previousValue + ", newValue=" + newValue
                + ", occurredAt=" + occurredAt + "]";
    }

}
