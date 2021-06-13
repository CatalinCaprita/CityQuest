package caprita.catalin.cityquest.ui.models;

import java.time.LocalDate;
import java.util.List;

public class UserModel {
    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String gender;

    private double oteScore;

    private double conScore;

    private double extScore;

    private double agrScore;

    private double neurScore;

    private boolean isEnabled;

    private boolean isAlone;

    private int registrationStep = -1;

    private String joinDate;

    private Integer knowledge;

    private Integer vitality;

    private Integer swiftness;

    private Integer sociability;

    private List<UserCompanion> companions;

    private int totalFinishedQuests;

    public UserModel(Long id, String firstName, String lastName, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public UserModel() {
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getOteScore() {
        return oteScore;
    }

    public void setOteScore(double oteScore) {
        this.oteScore = oteScore;
    }

    public double getConScore() {
        return conScore;
    }

    public void setConScore(double conScore) {
        this.conScore = conScore;
    }

    public double getExtScore() {
        return extScore;
    }

    public void setExtScore(double extScore) {
        this.extScore = extScore;
    }

    public double getAgrScore() {
        return agrScore;
    }

    public void setAgrScore(double agrScore) {
        this.agrScore = agrScore;
    }

    public double getNeurScore() {
        return neurScore;
    }

    public void setNeurScore(double neurScore) {
        this.neurScore = neurScore;
    }


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getRegistrationStep() {
        return registrationStep;
    }

    public void setRegistrationStep(int registrationStep) {
        this.registrationStep = registrationStep;
    }

    public boolean isAlone() {
        return isAlone;
    }

    public void setAlone(boolean alone) {
        isAlone = alone;
    }

    public List<UserCompanion> getCompanions() {
        return companions;
    }

    public void setCompanions(List<UserCompanion> companions) {
        this.companions = companions;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public Integer getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(Integer knowledge) {
        this.knowledge = knowledge;
    }

    public Integer getVitality() {
        return vitality;
    }

    public void setVitality(Integer vitality) {
        this.vitality = vitality;
    }

    public Integer getSwiftness() {
        return swiftness;
    }

    public void setSwiftness(Integer swiftness) {
        this.swiftness = swiftness;
    }

    public Integer getSociability() {
        return sociability;
    }

    public void setSociability(Integer sociability) {
        this.sociability = sociability;
    }

    public int getTotalFinishedQuests() {
        return totalFinishedQuests;
    }

    public void setTotalFinishedQuests(int totalFinishedQuests) {
        this.totalFinishedQuests = totalFinishedQuests;
    }
}
