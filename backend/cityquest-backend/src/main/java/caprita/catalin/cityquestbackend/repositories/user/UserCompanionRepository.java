package caprita.catalin.cityquestbackend.repositories.user;

import caprita.catalin.cityquestbackend.domain.entities.user.UserCompanion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCompanionRepository extends JpaRepository<UserCompanion, Long> {
}
