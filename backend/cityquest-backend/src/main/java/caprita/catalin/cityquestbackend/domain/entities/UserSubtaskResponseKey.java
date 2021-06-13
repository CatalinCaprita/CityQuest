package caprita.catalin.cityquestbackend.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class UserSubtaskResponseKey implements Serializable {

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "SUBTASK_ID")
    private Long subtaskId;

    @Column(name = "QUEST_ID")
    private Long questId;

    public UserSubtaskResponseKey(Long userId, Long subtaskId, Long questId) {
        this.userId = userId;
        this.subtaskId = subtaskId;
        this.questId = questId;
    }

    public UserSubtaskResponseKey() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSubtaskResponseKey)) return false;
        UserSubtaskResponseKey that = (UserSubtaskResponseKey) o;
        return getUserId().equals(that.getUserId()) && getSubtaskId().equals(that.getSubtaskId()) && getQuestId().equals(that.getQuestId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getSubtaskId(), getQuestId());
    }
}
