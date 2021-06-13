package caprita.catalin.cityquestbackend.repositories.location;

import caprita.catalin.cityquestbackend.domain.entities.Location;
import caprita.catalin.cityquestbackend.domain.entities.Location_;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LocationSpecificationRepositoryImpl implements LocationSpecificationRepository{
    private final LocationRepository locationRepository;

    public LocationSpecificationRepositoryImpl(@Lazy LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Optional<Location> findByIdWithQuest(Long id) {

        Specification<Location> spec = (root, cq, cb) ->{
            root.fetch(Location_.QUEST);
            return cb.equal(root.get(Location_.ID), id);
        };
        return Optional.ofNullable(locationRepository.findAll(spec).get(0));
    }
}
