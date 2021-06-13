package caprita.catalin.cityquestbackend.repositories.quest;

import caprita.catalin.cityquestbackend.domain.entities.quest.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
}
