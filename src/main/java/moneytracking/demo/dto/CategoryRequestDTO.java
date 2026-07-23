package moneytracking.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CategoryRequestDTO {
    @NotNull(message = "Name is a required field")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    private String description;

    private Long typeId;
    private Long userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CategoryRequestDTO [name=" + name + ", description=" + description + ", typeId=" + typeId + ", userId="
                + userId + "]";
    }

}
