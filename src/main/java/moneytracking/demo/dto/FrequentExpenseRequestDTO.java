package moneytracking.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FrequentExpenseRequestDTO {
    private Long userId;
    private Long categoryId;
    private String name;

    @NotNull(message = "Amount is a required field")
    @Min(value = 1, message = "Amount must be higuer than zero")
    private BigDecimal amount;

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
        return "FrequentExpenseRequestDTO [userId=" + userId + ", categoryId=" + categoryId + ", name=" + name
                + ", amount=" + amount + "]";
    }

}
