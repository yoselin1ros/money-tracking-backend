package moneytracking.demo.dto;

public class TemplateResponseDTO {
    private Long templateId;
    private String templateName;
    private TransactionResponseDTO draft;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public TransactionResponseDTO getDraft() {
        return draft;
    }

    public void setDraft(TransactionResponseDTO draft) {
        this.draft = draft;
    }

    @Override
    public String toString() {
        return "TemplateResponseDTO [templateId=" + templateId + ", templateName=" + templateName + ", draft=" + draft
                + "]";
    }

}
