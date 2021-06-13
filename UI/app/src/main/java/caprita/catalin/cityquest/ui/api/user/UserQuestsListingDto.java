package caprita.catalin.cityquest.ui.api.user;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import caprita.catalin.cityquest.ui.models.quest.QuestBriefModel;

public class UserQuestsListingDto implements Serializable {
    private Map<Integer, List<QuestBriefModel>> questsByDay;

    public Map<Integer, List<QuestBriefModel>> getQuestsByDay() {
        return questsByDay;
    }

    public void setQuestsByDay(Map<Integer, List<QuestBriefModel>> questsByDay) {
        this.questsByDay = questsByDay;
    }
}
