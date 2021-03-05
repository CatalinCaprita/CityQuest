package caprita.catalin.cityquestbackend.repositories;

import caprita.catalin.cityquestbackend.domain.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location,Long> {
    Optional<Location> findByName(String name);
    Location getByName(String name);
}
