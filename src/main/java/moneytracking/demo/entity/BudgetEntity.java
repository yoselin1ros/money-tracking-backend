package moneytracking.demo.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
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
public class BudgetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Creates user_id foreign key column
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Column(name = "spending_limit", nullable = false)
    private BigDecimal spending_limit = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_type_ref_item_id")
    private RefItemEntity periodType;

    @Column(name = "period_start")
    private LocalDate periodStart = LocalDate.now();

    @Column(name = "period_end")
    private LocalDate periodEnd = LocalDate.now();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public BigDecimal getSpending_limit() {
        return spending_limit;
    }

    public RefItemEntity getPeriodType() {
        return periodType;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "BudgetEntity [id=" + id + ", user=" + user + ", category=" + category + ", spending_limit="
                + spending_limit + ", periodType=" + periodType + ", periodStart=" + periodStart + ", periodEnd="
                + periodEnd + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

}
