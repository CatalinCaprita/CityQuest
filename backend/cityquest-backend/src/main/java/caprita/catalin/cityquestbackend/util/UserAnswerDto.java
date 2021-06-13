package caprita.catalin.cityquestbackend.util;

import liquibase.sqlgenerator.core.AddUniqueConstraintGeneratorInformix;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static caprita.catalin.cityquestbackend.util.Constants.Form.*;

public class UserAnswerDto {

    public String timestamp;
    public int[] personalityTest = new int[Constants.Form.SIZES[BFI]];
    public Map<String, Integer> locationRatings = new HashMap<>();
    public String restaurantsRecs;
    public String clubsRecs;
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int[] getPersonalityTest() {
        return personalityTest;
    }

    public void setPersonalityTest(int[] personalityTest) {
        this.personalityTest = personalityTest;
    }

    public Map<String, Integer> getLocationRatings() {
        return locationRatings;
    }

    public void setLocationRatings(Map<String, Integer> locationRatings) {
        this.locationRatings = locationRatings;
    }

    public String getRestaurantsRecs() {
        return restaurantsRecs;
    }

    public void setRestaurantsRecs(String restaurantsRecs) {
        this.restaurantsRecs = restaurantsRecs;
    }

    public String getClubsRecs() {
        return clubsRecs;
    }

    public void setClubsRecs(String clubsRecs) {
        this.clubsRecs = clubsRecs;
    }
}
