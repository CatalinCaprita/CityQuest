package caprita.catalin.cityquestbackend.repositories.user;

import caprita.catalin.cityquestbackend.domain.entities.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.JoinType;
import java.util.Optional;

@Repository
public class UserSpecRepositoryImpl implements UserSpecRepository {
    private final UserRepository userRepository;
    private final UserCompanionRepository userCompanionRepository;

    @Autowired
    public UserSpecRepositoryImpl(@Lazy UserRepository userRepository,
                                  @Lazy UserCompanionRepository userCompanionRepository) {
        this.userRepository = userRepository;
        this.userCompanionRepository = userCompanionRepository;
    }

    @Override
    public Optional<User> findLazyForDetailsDtoById(Long id) {
        Specification<User> spec = (root, criteriaQuery, criteriaBuilder) ->{
            root.fetch("roles", JoinType.INNER);
            root.fetch("companions", JoinType.INNER);
            return criteriaBuilder.equal(root.get("id"), id);
        };
        return userRepository.findAll(spec).stream().findFirst();
    }
}
