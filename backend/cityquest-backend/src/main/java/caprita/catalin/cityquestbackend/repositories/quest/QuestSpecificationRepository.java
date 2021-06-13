package caprita.catalin.cityquestbackend.repositories.quest;

import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import caprita.catalin.cityquestbackend.domain.enums.QuestStatus;

import java.util.List;
import java.util.Optional;

public interface QuestSpecificationRepository {
    Optional<Quest> findWithSubtasksById(Long id);
    List<Quest> findAllByUserIdAndStatus(Long userId, QuestStatus status);
}
