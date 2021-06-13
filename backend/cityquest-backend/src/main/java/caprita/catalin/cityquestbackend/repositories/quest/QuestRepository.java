package caprita.catalin.cityquestbackend.repositories.quest;

import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuestRepository extends QuestSpecificationRepository,
        JpaRepository<Quest, Long>, JpaSpecificationExecutor<Quest> {
}
