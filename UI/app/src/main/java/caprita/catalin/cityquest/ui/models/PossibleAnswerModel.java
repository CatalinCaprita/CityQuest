package caprita.catalin.cityquest.ui.models;

import java.io.Serializable;

public class PossibleAnswerModel implements Serializable {
    private Long id;
    private String content;
    private Boolean isCorrect;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }
}
