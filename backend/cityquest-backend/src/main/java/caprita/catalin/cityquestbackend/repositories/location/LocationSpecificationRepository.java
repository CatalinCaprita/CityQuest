package caprita.catalin.cityquestbackend.repositories.location;

import caprita.catalin.cityquestbackend.domain.entities.Location;

import java.util.Optional;

public interface LocationSpecificationRepository {
    Optional<Location> findByIdWithQuest(Long id);
}
