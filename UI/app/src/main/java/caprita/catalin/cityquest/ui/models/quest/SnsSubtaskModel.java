package caprita.catalin.cityquest.ui.models.quest;

public class SnsSubtaskModel {
    private String subtaskContent;
    private Long subtaskId;
    private Long uniqueAnswerId;
    private String uniqueAnswerContent;
    private Long userAnswerId;

    public Long getUserAnswerId() {
        return userAnswerId;
    }

    public void setUserAnswerId(Long userAnswerId) {
        this.userAnswerId = userAnswerId;
    }

    public String getSubtaskContent() {
        return subtaskContent;
    }

    public void setSubtaskContent(String subtaskContent) {
        this.subtaskContent = subtaskContent;
    }

    public Long getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(Long subtaskId) {
        this.subtaskId = subtaskId;
    }

    public Long getUniqueAnswerId() {
        return uniqueAnswerId;
    }

    public void setUniqueAnswerId(Long uniqueAnswerId) {
        this.uniqueAnswerId = uniqueAnswerId;
    }

    public String getUniqueAnswerContent() {
        return uniqueAnswerContent;
    }

    public void setUniqueAnswerContent(String uniqueAnswerContent) {
        this.uniqueAnswerContent = uniqueAnswerContent;
    }
}
