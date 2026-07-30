package moneytracking.demo.dto;

import java.math.BigDecimal;

public class BudgetResponseDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal spendingLimit;
    private Long periodTypeId;
    private String periodTypeName;
    private String periodStart;
    private String periodEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getPeriodTypeName() {
        return periodTypeName;
    }

    public void setPeriodTypeName(String periodTypeName) {
        this.periodTypeName = periodTypeName;
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
        return "BudgetResponseDTO [id=" + id + ", categoryId=" + categoryId + ", categoryName=" + categoryName
                + ", spendingLimit=" + spendingLimit + ", periodTypeId=" + periodTypeId + ", periodTypeName="
                + periodTypeName + ", periodStart=" + periodStart + ", periodEnd=" + periodEnd + "]";
    }

}
