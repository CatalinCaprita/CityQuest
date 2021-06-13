package caprita.catalin.cityquestbackend.repositories.user;

import caprita.catalin.cityquestbackend.domain.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> , JpaSpecificationExecutor<User>, UserSpecRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);

}
