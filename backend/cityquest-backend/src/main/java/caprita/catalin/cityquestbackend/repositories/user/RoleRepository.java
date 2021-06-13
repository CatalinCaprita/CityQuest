package caprita.catalin.cityquestbackend.repositories.user;

import caprita.catalin.cityquestbackend.domain.entities.user.Role;
import caprita.catalin.cityquestbackend.domain.enums.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(RoleCode code);
}
