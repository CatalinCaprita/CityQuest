package caprita.catalin.cityquest.ui.models.quest;

import java.io.Serializable;
import java.util.List;

public class UserQuestResultModel implements Serializable {
    private Long questId;
    private List<UserSubtaskResultModel> results;
    private String primaryRewardType;
    private Integer primaryRewardAmount;
    private String secondaryRewardType;
    private Integer secondaryRewardAmount;

    public Long getQuestId() {
        return questId;
    }

    public void setQuestId(Long questId) {
        this.questId = questId;
    }

    public List<UserSubtaskResultModel> getResults() {
        return results;
    }

    public void setResults(List<UserSubtaskResultModel> results) {
        this.results = results;
    }

    public String getPrimaryRewardType() {
        return primaryRewardType;
    }

    public void setPrimaryRewardType(String primaryRewardType) {
        this.primaryRewardType = primaryRewardType;
    }

    public Integer getPrimaryRewardAmount() {
        return primaryRewardAmount;
    }

    public void setPrimaryRewardAmount(Integer primaryRewardAmount) {
        this.primaryRewardAmount = primaryRewardAmount;
    }

    public String getSecondaryRewardType() {
        return secondaryRewardType;
    }

    public void setSecondaryRewardType(String secondaryRewardType) {
        this.secondaryRewardType = secondaryRewardType;
    }

    public Integer getSecondaryRewardAmount() {
        return secondaryRewardAmount;
    }

    public void setSecondaryRewardAmount(Integer secondaryRewardAmount) {
        this.secondaryRewardAmount = secondaryRewardAmount;
    }
}
