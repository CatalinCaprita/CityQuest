package caprita.catalin.cityquestbackend.domain.entities;

import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import caprita.catalin.cityquestbackend.domain.enums.QuestStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "USER_QUEST_LOG")
public class UserQuestLog {

    @EmbeddedId
    private UserQuestLogKey id;

    @ManyToOne
    @MapsId("QUEST_ID")
    @JoinColumn(name = "QUEST_ID")
    private Quest quest;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private QuestStatus progress;

    @Column(name = "COMPLETION_DATE")
    private LocalDate completionDate;

}
