package caprita.catalin.cityquestbackend.controllers.dto.location.quest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserSubtaskResultDto implements Serializable {
    private Long subtaskId;
    private Long correctAnswerId;
    private Long userAnswerId;
    private Boolean userCorrect = false;
    private String correctAnswerContent;
    private String userAnswerContent;
    private String subtaskContent;
}
