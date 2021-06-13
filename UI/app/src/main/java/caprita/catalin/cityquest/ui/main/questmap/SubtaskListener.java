package caprita.catalin.cityquest.ui.main.questmap;

public interface SubtaskListener {
    void onSubtaskCompleted(Long possibleAnswerId);
    void onSubtaskError();
    void onSubtaskUpdate(Long possibleAnswerId);
    void onSubtaskUpdate(Long possibleAnswerId, String userAnswerValue);
}
