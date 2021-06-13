package caprita.catalin.cityquestbackend.repositories.quest;

import caprita.catalin.cityquestbackend.domain.entities.*;
import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import caprita.catalin.cityquestbackend.domain.entities.quest.Quest_;
import caprita.catalin.cityquestbackend.domain.enums.QuestStatus;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@Repository
public class QuestSpecificationRepositoryImpl implements QuestSpecificationRepository{
    private  final QuestRepository questRepository;

    public QuestSpecificationRepositoryImpl( @Lazy QuestRepository questRepository) {
        this.questRepository = questRepository;
    }

    @Override
    public Optional<Quest> findWithSubtasksById(Long id) {
        Specification<Quest> spec = (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            root.fetch(Quest_.SUBTASKS, JoinType.INNER);
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.equal(root.get(Quest_.ID), id));
            return predicate;
        };
        return Optional.ofNullable(questRepository.findAll(spec).get(0));
    }

    @Override
    public List<Quest> findAllByUserIdAndStatus(Long userId, QuestStatus status) {
        Specification<Quest> spec = (root, query, builder) ->{
            Join<Quest, UserQuestLog> questToLogs = root.join(Quest_.LOGS);
            Predicate pred = builder.equal(
                    questToLogs.get(UserQuestLog_.ID).get(UserQuestLogKey_.USER_ID),
                    userId);
            pred = builder.and(pred,
                    builder.equal(questToLogs.get(UserQuestLog_.PROGRESS), status)
            );
            query.distinct(true);
            return pred;
        };
        return questRepository.findAll(spec);
    }
}
