package caprita.catalin.cityquest.ui.models;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BfpiQuestion {
    private String questionText;
    private int index;
    private int selectedAnswer;
    private int answerValue;
    public BfpiQuestion() {
    }

    public BfpiQuestion(int index, int selectedAnswer) {
        this.index = index;
        this.selectedAnswer = selectedAnswer;
    }

    public int getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(int answerValue) {
        this.answerValue = answerValue;
    }

    public int getIndex() {
        return index;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BfpiQuestion)) return false;
        BfpiQuestion that = (BfpiQuestion) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
