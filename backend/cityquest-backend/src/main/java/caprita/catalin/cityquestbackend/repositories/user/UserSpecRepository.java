package caprita.catalin.cityquestbackend.repositories.user;

import caprita.catalin.cityquestbackend.domain.entities.user.User;

import java.util.Optional;

public interface UserSpecRepository {
    Optional<User> findLazyForDetailsDtoById(Long id);

}
