package caprita.catalin.cityquestbackend.services.location;

import caprita.catalin.cityquestbackend.domain.entities.Location;
import caprita.catalin.cityquestbackend.repositories.location.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService{
    private final LocationRepository locationRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);
    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location save(Location location) {
        Location existing = locationRepository.findByName(location.getName()).orElse(null);
        if(existing == null) {
            location = locationRepository.save(location);
            LOGGER.info("Saved NEW Location {} of category {}", location.getName(), location.getCategory());
            return location;
        }
        location.setId(existing.getId());
        LOGGER.debug("UPDATED location {}" ,location.getName());
        return locationRepository.save(location);
    }

    @Override
    public Location findByName(String locationName){
        return locationRepository.findByName(locationName)
                .orElse(null);
//                .orElseThrow(() -> new NotFoundException(String.format("No location with name {}", locationName)));
    }

    @Override
    public Location findById(Long locationId) {
        return locationRepository.findById(locationId)
                .orElse(null);
//                .orElseThrow(() -> new NotFoundException(String.format("No location with id {}", locationId)));
    }

    @Override
    public Location findWithQuestBriefById(Long locationId) throws NoSuchElementException, InternalError {
        Location location = locationRepository.findByIdWithQuest(locationId)
                .orElseThrow();
        return location;
    }

    @Override
    public List<Long> getAllIds() {
        return locationRepository.findAll().stream()
                .map(Location::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCategories() {
        return locationRepository.findAll()
                .stream()
                .map(location -> location.getCategory().toString())
                .collect(Collectors.toList());
    }
}
