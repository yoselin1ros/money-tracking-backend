package moneytracking.demo.dto;

public class AccountResponseDTO {
    private Long id;
    private String name;
    private Long typeId;
    private String typeName;
    private Integer initialBalance;
    private Integer currentBalance;

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

    public Integer getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Integer initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    @Override
    public String toString() {
        return "AccountResponseDTO [id=" + id + ", name=" + name + ", typeId=" + typeId + ", typeName=" + typeName
                + ", initialBalance=" + initialBalance + ", currentBalance=" + currentBalance + "]";
    }

}
