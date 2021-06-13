package caprita.catalin.cityquestbackend.domain.entities.quest;

import caprita.catalin.cityquestbackend.domain.entities.Location;
import caprita.catalin.cityquestbackend.domain.entities.UserQuestLog;
import caprita.catalin.cityquestbackend.domain.entities.UserSubtaskResponse;
import caprita.catalin.cityquestbackend.domain.enums.QuestType;
import caprita.catalin.cityquestbackend.domain.enums.RewardType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "QUEST")
public class Quest{

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "ID")
    private Location location;

    @Column(name=   "LOCATION_NAME")
    private String locationName;

    @Column(name ="TITLE")
    private String title;

    @Column(name = "QUEST_TYPE")
    @Enumerated(EnumType.STRING)
    private QuestType type;

    @Column(name = "DURATION", columnDefinition = "tinyint default 1")
    private Integer duration;

    @Column(name = "PRIMARY_REWARD_TYPE")
    @Enumerated(EnumType.STRING)
    public RewardType primaryRewardType;
    @Column(name = "PRIMARY_REWARD_AMOUNT")
    public Integer primaryRewardAmount;

    @Column(name = "SECONDARY_REWARD_TYPE", nullable = true)
    @Enumerated(EnumType.STRING)
    public RewardType secondaryRewardType;
    @Column(name = "SECONDARY_REWARD_AMOUNT", columnDefinition = "tinyint default 0")
    public Integer secondaryRewardAmount;

    @Column(name = "LATITUDE", scale = 14, precision = 16)
    private BigDecimal locationLat;
    @Column(name = "LONGITUDE",scale = 14, precision = 16)
    private BigDecimal locationLng;

    @OneToMany(fetch = FetchType.LAZY,
            orphanRemoval = true,
            mappedBy = "quest",
            cascade = CascadeType.ALL)
    private List<Subtask> subtasks = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            orphanRemoval = true,
            mappedBy = "quest")
    private Set<UserQuestLog> logs;

    public void addSubtask(Subtask subtask){
        this.subtasks.add(subtask);
        subtask.setQuest(this);
    }

    public void removeSubtask(Subtask subtask){
        this.subtasks.remove(subtask);
        subtask.setQuest(null);
    }
}
