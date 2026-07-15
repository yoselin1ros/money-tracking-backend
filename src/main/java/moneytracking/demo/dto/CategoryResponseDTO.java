package moneytracking.demo.dto;

public class CategoryResponseDTO {
    private Long id;
    private String name;
    private Long typeId;
    private String typeName;

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

    @Override
    public String toString() {
        return "CategoryResponseDTO [id=" + id + ", name=" + name + ", typeId=" + typeId + ", typeName=" + typeName
                + "]";
    }

}
