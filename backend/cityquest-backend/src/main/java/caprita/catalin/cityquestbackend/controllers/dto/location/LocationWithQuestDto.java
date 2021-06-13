package caprita.catalin.cityquestbackend.controllers.dto.location;

import caprita.catalin.cityquestbackend.controllers.dto.location.quest.QuestBriefDto;
import caprita.catalin.cityquestbackend.domain.enums.LocationCategory;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LocationWithQuestDto implements Serializable {
    private Long id;
    private String name;
    private LocationCategory category;
    private QuestBriefDto questBriefDto;

}
