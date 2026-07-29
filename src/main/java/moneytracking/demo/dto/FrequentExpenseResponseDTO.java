package moneytracking.demo.dto;

import java.math.BigDecimal;

public class FrequentExpenseResponseDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private BigDecimal amount;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "FrequentExpenseResponseDTO [id=" + id + ", categoryId=" + categoryId + ", categoryName=" + categoryName
                + ", name=" + name + ", amount=" + amount + "]";
    }

}
