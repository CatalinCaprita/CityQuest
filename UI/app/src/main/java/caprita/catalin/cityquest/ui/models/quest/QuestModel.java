package caprita.catalin.cityquest.ui.models.quest;

import java.io.Serializable;
import java.util.List;

public class QuestModel implements Serializable {
    private Long id;
    private String title;
    private String type;
    private Integer duration;
    private String locationName;
    private String primaryRewardType;
    private Integer primaryRewardAmount;
    private String secondaryRewardType;
    private Integer secondaryRewardAmount;
    private List<SubtaskModel> subtasks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public List<SubtaskModel> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<SubtaskModel> subtasks) {
        this.subtasks = subtasks;
    }
}
