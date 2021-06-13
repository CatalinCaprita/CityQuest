package caprita.catalin.cityquestbackend.repositories.user_quest;

import caprita.catalin.cityquestbackend.domain.entities.UserQuestLog;
import caprita.catalin.cityquestbackend.domain.entities.UserQuestLogKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuestLogRepository extends JpaRepository<UserQuestLog, UserQuestLogKey> {
}
