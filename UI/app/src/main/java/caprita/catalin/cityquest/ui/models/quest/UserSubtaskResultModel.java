package caprita.catalin.cityquest.ui.models.quest;

public class UserSubtaskResultModel {
    private Long subtaskId;
    private Long correctAnswerId;
    private Long userAnswerId;
    private Boolean userCorrect = false;
    private String correctAnswerContent;
    private String userAnswerContent;
    private String subtaskContent;

    public Long getUserAnswerId() {
        return userAnswerId;
    }

    public void setUserAnswerId(Long userAnswerId) {
        this.userAnswerId = userAnswerId;
    }

    public String getUserAnswerContent() {
        return userAnswerContent;
    }

    public void setUserAnswerContent(String userAnswerContent) {
        this.userAnswerContent = userAnswerContent;
    }

    public Long getCorrectAnswerId() {
        return correctAnswerId;
    }

    public void setCorrectAnswerId(Long correctAnswerId) {
        this.correctAnswerId = correctAnswerId;
    }

    public Long getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(Long subtaskId) {
        this.subtaskId = subtaskId;
    }

    public Boolean getUserCorrect() {
        return userCorrect;
    }

    public void setUserCorrect(Boolean userCorrect) {
        this.userCorrect = userCorrect;
    }

    public String getCorrectAnswerContent() {
        return correctAnswerContent;
    }

    public void setCorrectAnswerContent(String correctAnswerContent) {
        this.correctAnswerContent = correctAnswerContent;
    }

    public String getSubtaskContent() {
        return subtaskContent;
    }

    public void setSubtaskContent(String subtaskContent) {
        this.subtaskContent = subtaskContent;
    }
}
