package moneytracking.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TransactionRequestDTO {
    private Long userId;
    private Long accountId;
    private Long categoryId;

    @NotNull(message = "Amount is a required field")
    @Min(value = 1, message = "Ammount must be higuer than zero")
    private Integer amount;

    private String note;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "TransactionRequestDTO [userId=" + userId + ", accountId=" + accountId + ", categoryId=" + categoryId
                + ", amount=" + amount + ", note=" + note + "]";
    }

}
