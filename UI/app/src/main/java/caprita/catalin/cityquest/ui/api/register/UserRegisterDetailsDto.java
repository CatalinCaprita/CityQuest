package caprita.catalin.cityquest.ui.api.register;


import java.util.List;

import caprita.catalin.cityquest.ui.models.UserCompanion;

public class UserRegisterDetailsDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String gender;

    int[] quizResponses;

    private boolean isAlone;

    private List<UserCompanion> companions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int[] getQuizResponses() {
        return quizResponses;
    }

    public void setQuizResponses(int[] quizResponses) {
        this.quizResponses = quizResponses;
    }

    public List<UserCompanion> getCompanions() {
        return companions;
    }

    public void setCompanions(List<UserCompanion> companions) {
        this.companions = companions;
    }

    public boolean isAlone() {
        return isAlone;
    }

    public void setAlone(boolean alone) {
        isAlone = alone;
    }
}
