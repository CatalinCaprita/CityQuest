package caprita.catalin.cityquestbackend.domain.enums;

import java.util.Arrays;
import java.util.List;

public enum QuestType {
    QUIZ,
    STROLL_AND_SEE,
    GUESSTIMATE;

    public static List<QuestType> valuesList(){return Arrays.asList(QuestType.values()) ;}
}
