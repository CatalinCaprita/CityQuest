package caprita.catalin.cityquestbackend.services.location;

import caprita.catalin.cityquestbackend.domain.entities.Location;

import java.util.List;
import java.util.NoSuchElementException;

public interface LocationService {
    Location save(Location location);

    Location findByName(String locationName);
    Location findById(Long locationId);
    Location findWithQuestBriefById(Long locationId) throws NoSuchElementException, InternalError;

    List<Long> getAllIds();
    List<String> getAllCategories();

}
