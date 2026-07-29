package moneytracking.demo.dto;

import java.math.BigDecimal;

public class AccountResponseDTO {
    private Long id;
    private String name;
    private Long typeId;
    private String typeName;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    @Override
    public String toString() {
        return "AccountResponseDTO [id=" + id + ", name=" + name + ", typeId=" + typeId + ", typeName=" + typeName
                + ", initialBalance=" + initialBalance + ", currentBalance=" + currentBalance + "]";
    }

}
