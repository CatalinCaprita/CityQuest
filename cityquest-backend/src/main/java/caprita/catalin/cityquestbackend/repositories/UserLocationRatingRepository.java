package caprita.catalin.cityquestbackend.repositories;

import caprita.catalin.cityquestbackend.domain.entities.UserLocationKey;
import caprita.catalin.cityquestbackend.domain.entities.UserLocationRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLocationRatingRepository extends JpaRepository<UserLocationRating, UserLocationKey> {

}
