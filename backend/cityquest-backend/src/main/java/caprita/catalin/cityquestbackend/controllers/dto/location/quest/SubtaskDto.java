package caprita.catalin.cityquestbackend.controllers.dto.location.quest;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SubtaskDto implements Serializable {
    private Long id;
    private String description;
    private List<PossibleAnswerDto> possibleAnswers;
}
