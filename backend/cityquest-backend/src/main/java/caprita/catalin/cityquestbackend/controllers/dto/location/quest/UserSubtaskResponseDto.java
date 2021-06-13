package caprita.catalin.cityquestbackend.controllers.dto.location.quest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserSubtaskResponseDto implements Serializable {
    private Long subtaskId;
    private Long userAnswerId;
    private String userAnswerValue;
}
