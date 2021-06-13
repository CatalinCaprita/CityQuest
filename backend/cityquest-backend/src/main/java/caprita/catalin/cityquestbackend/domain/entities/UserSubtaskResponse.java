package caprita.catalin.cityquestbackend.domain.entities;

import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import caprita.catalin.cityquestbackend.domain.entities.quest.Subtask;
import caprita.catalin.cityquestbackend.domain.entities.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "USER_SUBTASK_RESPONSE")
@Getter
@Setter
public class UserSubtaskResponse {

    @EmbeddedId
    private UserSubtaskResponseKey id;

    @ManyToOne
    @MapsId("USER_ID")
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @MapsId("QUEST_ID")
    @JoinColumn(name = "QUEST_ID")
    private Quest quest;

    @ManyToOne
    @MapsId("SUBTASK_ID")
    @JoinColumn(name = "SUBTASK_ID")
    private Subtask subtask;

    @Column(name = "CORRECT")
    private Boolean correct;

}
