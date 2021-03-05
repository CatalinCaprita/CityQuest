package caprita.catalin.cityquestbackend.services.user;

import caprita.catalin.cityquestbackend.domain.entities.User;

public interface UserService {
    User findById(Long userId);
    User create(User user);
}
