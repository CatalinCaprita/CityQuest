package caprita.catalin.cityquest.ui.models.quest;

import java.io.Serializable;
import java.util.List;

import caprita.catalin.cityquest.ui.models.PossibleAnswerModel;

public class SubtaskModel implements Serializable {
    private Long id;
    private String description;
    private List<PossibleAnswerModel> possibleAnswers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PossibleAnswerModel> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(List<PossibleAnswerModel> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }
}
