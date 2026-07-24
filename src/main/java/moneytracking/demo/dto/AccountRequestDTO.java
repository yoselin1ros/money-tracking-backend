package moneytracking.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AccountRequestDTO {
    private Long userId;
    
    @NotNull(message = "Name is a required field")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    @NotNull(message = "Type is a required field")
    private Long typeId;
    
    private Integer initialBalance;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Integer getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Integer initialBalance) {
        this.initialBalance = initialBalance;
    }

    @Override
    public String toString() {
        return "AccountRequestDTO [userId=" + userId + ", name=" + name + ", typeId=" + typeId + ", initialBalance="
                + initialBalance + "]";
    }

}
