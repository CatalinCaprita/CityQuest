package caprita.catalin.cityquestbackend.controllers.dto.location;

import caprita.catalin.cityquestbackend.domain.enums.LocationCategory;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LocationListingDTO implements Serializable {
    private String name;
    private Long id;
    private LocationCategory category;
}
