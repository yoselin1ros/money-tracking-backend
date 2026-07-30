package moneytracking.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BudgetRequestDTO {
    private Long userId;
    private Long categoryId;

    @NotNull(message = "Spending limit is a required field")
    @Min(value = 1, message = "Spending limit must be higuer than zero")
    private BigDecimal spendingLimit;

    private Long periodTypeId;
    private String periodStart;
    private String periodEnd;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(BigDecimal spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public Long getPeriodTypeId() {
        return periodTypeId;
    }

    public void setPeriodTypeId(Long periodTypeId) {
        this.periodTypeId = periodTypeId;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }

    @Override
    public String toString() {
        return "BudgetRequestDTO [userId=" + userId + ", categoryId=" + categoryId + ", spendingLimit=" + spendingLimit
                + ", periodTypeId=" + periodTypeId + ", periodStart=" + periodStart + ", periodEnd=" + periodEnd + "]";
    }

}
