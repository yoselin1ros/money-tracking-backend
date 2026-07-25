package moneytracking.demo.dto;

public class TransactionFilterDTO {
    private Long categoryId;
    private String periodName;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    @Override
    public String toString() {
        return "TransactionFilterDTO [categoryId=" + categoryId + ", periodName=" + periodName + "]";
    }

}
