package caprita.catalin.cityquestbackend.controllers.dto.location.quest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserQuestResponsesDto implements Serializable {
    private Long userId;
    private Long questId;
    private String completionDate;
    private List<UserSubtaskResponseDto> responses;

}
