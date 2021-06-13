package caprita.catalin.cityquest.ui.models.quest;

import java.util.List;

public class UserQuestResponseModel {
    private Long userId;
    private Long questId;
    private String completionDate;
    private List<UserSubtaskResponseModel> responses;

    public Long getQuestId() {
        return questId;
    }

    public void setQuestId(Long questId) {
        this.questId = questId;
    }

    public List<UserSubtaskResponseModel> getResponses() {
        return responses;
    }

    public void setResponses(List<UserSubtaskResponseModel> responses) {
        this.responses = responses;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }
}
