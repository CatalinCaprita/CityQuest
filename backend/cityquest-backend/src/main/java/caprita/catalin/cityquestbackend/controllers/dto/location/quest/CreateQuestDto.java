package caprita.catalin.cityquestbackend.controllers.dto.location.quest;

import caprita.catalin.cityquestbackend.domain.enums.QuestType;
import caprita.catalin.cityquestbackend.domain.enums.RewardType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateQuestDto implements Serializable {
    private Long locationId;
    private String title;
    private String type;
    private Integer duration;
    public String primaryRewardType;
    public Integer primaryRewardAmount;
    public String secondaryRewardType;
    public Integer secondaryRewardAmount;
    private BigDecimal locationLat;
    private BigDecimal locationLng;

    private List<CreateSubtaskDto> subtasks;
}
