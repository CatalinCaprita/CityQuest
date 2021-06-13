package caprita.catalin.cityquestbackend.controllers.dto.location.quest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserQuestResultDto implements Serializable {
    private Long questId;
    private List<UserSubtaskResultDto> results;
    private String primaryRewardType;
    private Integer primaryRewardAmount;
    private String secondaryRewardType;
    private Integer secondaryRewardAmount;
}
