package caprita.catalin.cityquestbackend.domain.entities.quest;

import caprita.catalin.cityquestbackend.domain.entities.BaseEntity;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "POSSIBLE_ANSWER")
public class PossibleAnswer extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "SUBTASK_ID")
    private Subtask subtask;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "IS_CORRECT")
    private Boolean isCorrect;

}
