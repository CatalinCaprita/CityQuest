package caprita.catalin.cityquestbackend.domain.entities.quest;

import caprita.catalin.cityquestbackend.domain.entities.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "SUBTASK")
public class Subtask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUEST_ID",
    referencedColumnName = "ID")
    private Quest quest;

    @Column(name = "DESCRIPTION")
    private String description;

    @OneToMany(fetch = FetchType.EAGER,
    mappedBy = "subtask",
    orphanRemoval = true,
    cascade = CascadeType.ALL)
    private List<PossibleAnswer> possibleAnswers = new ArrayList<>();

    public void addPossibleAnswer(PossibleAnswer pa){
        pa.setSubtask(this);
        this.possibleAnswers.add(pa);
    }

}
