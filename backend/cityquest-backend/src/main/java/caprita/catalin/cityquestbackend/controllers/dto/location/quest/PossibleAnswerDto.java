package caprita.catalin.cityquestbackend.controllers.dto.location.quest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PossibleAnswerDto implements Serializable {
    private Long id;
    private String content;
    private Boolean isCorrect;
}
