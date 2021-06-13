package caprita.catalin.cityquest.ui.models.quest;

public class UserSubtaskResponseModel {
    private Long subtaskId;
    private Long userAnswerId;
    private String userAnswerValue;
    public Long getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(Long subtaskId) {
        this.subtaskId = subtaskId;
    }

    public Long getUserAnswerId() {
        return userAnswerId;
    }

    public void setUserAnswerId(Long userAnswerId) {
        this.userAnswerId = userAnswerId;
    }

    public String getUserAnswerValue() {
        return userAnswerValue;
    }

    public void setUserAnswerValue(String userAnswerValue) {
        this.userAnswerValue = userAnswerValue;
    }
}
