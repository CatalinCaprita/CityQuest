package caprita.catalin.cityquestbackend.services.location;

import caprita.catalin.cityquestbackend.domain.entities.Location;

public interface LocationService {
    Location save(Location location);

    Location findByName(String locationName);

}
