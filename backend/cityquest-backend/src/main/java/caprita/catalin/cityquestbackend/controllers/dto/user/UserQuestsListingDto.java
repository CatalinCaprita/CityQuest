package caprita.catalin.cityquestbackend.controllers.dto.user;

import caprita.catalin.cityquestbackend.controllers.dto.location.quest.QuestBriefDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserQuestsListingDto implements Serializable {
    private String questsStatus;
    private Map<Integer, List<QuestBriefDto>> questsByDay;
}
