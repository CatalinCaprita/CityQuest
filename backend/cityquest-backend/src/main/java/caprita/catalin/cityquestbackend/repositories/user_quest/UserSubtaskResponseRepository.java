package caprita.catalin.cityquestbackend.repositories.user_quest;

import caprita.catalin.cityquestbackend.domain.entities.UserSubtaskResponse;
import caprita.catalin.cityquestbackend.domain.entities.UserSubtaskResponseKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubtaskResponseRepository extends JpaRepository<UserSubtaskResponse, UserSubtaskResponseKey>, JpaSpecificationExecutor<UserSubtaskResponse>{
    List<UserSubtaskResponse> findAllByUser_Id(Long id);
    List<UserSubtaskResponse> findAllByUser_IdAndQuest_Id(Long userid, Long questId);
}
