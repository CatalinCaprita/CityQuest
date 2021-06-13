package caprita.catalin.cityquest.ui.models.quest;


import android.os.Parcelable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import caprita.catalin.cityquest.ui.models.enums.QuestType;
import caprita.catalin.cityquest.ui.models.enums.RewardType;


public class QuestBriefModel implements Serializable {
    private Long id;
    private String title;
    private String locationName;
    private String type;
    private Integer duration;
    private String completionDate;
    public String primaryRewardType;
    public Integer primaryRewardAmount;
    public String secondaryRewardType;
    public Integer secondaryRewardAmount;
    private BigDecimal locationLat;
    private BigDecimal locationLng;
//    Will be of type quest_{id} so when reacting inside journal fragment,
//    Whenever ther is a new QuestBriefModel, set this to getResources().getIdentifier("drawable/quest_" + id)
//    Will get the resourceId and will compute this one
    private int resourceId;

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

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
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

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public BigDecimal getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(BigDecimal locationLat) {
        this.locationLat = locationLat;
    }

    public BigDecimal getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(BigDecimal locationLng) {
        this.locationLng = locationLng;
    }
}
