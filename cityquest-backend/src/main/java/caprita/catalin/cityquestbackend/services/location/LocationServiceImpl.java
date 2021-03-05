package caprita.catalin.cityquestbackend.services.location;

import caprita.catalin.cityquestbackend.domain.entities.Location;
import caprita.catalin.cityquestbackend.repositories.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Location existing = locationRepository.getByName(location.getName());
        if(existing == null) {
            location = locationRepository.save(location);
            LOGGER.debug("Saved NEW Location {}", location.getName());
            return location;
        }
        existing.setName(location.getName());
        existing.setCategory(location.getCategory());
        LOGGER.debug("UPDATED location {}" ,location.getName());
        return existing;
    }

    @Override
    public Location findByName(String locationName) {
        return locationRepository.findByName(locationName).orElse(null);
    }
}
