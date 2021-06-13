package caprita.catalin.cityquestbackend.controllers.dto.location.quest;

import caprita.catalin.cityquestbackend.domain.entities.quest.PossibleAnswer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class CreateSubtaskDto implements Serializable {
    private String description;
    private List<PossibleAnswerDto> possibleAnswers;
}
