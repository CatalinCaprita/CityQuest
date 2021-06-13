package caprita.catalin.cityquestbackend.controllers.dto.location.quest;

import caprita.catalin.cityquestbackend.domain.enums.QuestType;
import caprita.catalin.cityquestbackend.domain.enums.RewardType;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class QuestDetailedDto implements Serializable {
    private Long id;
    private String title;
    private String type;
    private Integer duration;
    private String locationName;
    private String primaryRewardType;
    private Integer primaryRewardAmount;
    private String secondaryRewardType;
    private Integer secondaryRewardAmount;
    private BigDecimal locationLat;
    private BigDecimal locationLng;

    private List<SubtaskDto> subtasks;
}
