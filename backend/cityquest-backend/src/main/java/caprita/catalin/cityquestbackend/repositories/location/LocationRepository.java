package caprita.catalin.cityquestbackend.repositories.location;

import caprita.catalin.cityquestbackend.domain.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location,Long>,
        JpaSpecificationExecutor<Location>, LocationSpecificationRepository{
    Optional<Location> findByName(String name);
}
