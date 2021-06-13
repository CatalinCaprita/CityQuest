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
public class UserQuestLogKey implements Serializable {
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "QUEST_ID")
    private Long questId;

    public UserQuestLogKey(Long userId, Long questId) {
        this.userId = userId;
        this.questId = questId;
    }

    public UserQuestLogKey() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserQuestLogKey)) return false;
        UserQuestLogKey that = (UserQuestLogKey) o;
        return getUserId().equals(that.getUserId()) && getQuestId().equals(that.getQuestId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getQuestId());
    }
}
