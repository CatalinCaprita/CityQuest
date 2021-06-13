package caprita.catalin.cityquestbackend.config.mapping.implementation;

import caprita.catalin.cityquestbackend.config.mapping.PresentationMappingRule;
import caprita.catalin.cityquestbackend.controllers.dto.location.LocationListingDTO;
import caprita.catalin.cityquestbackend.domain.entities.Location;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class LocationMappingRule implements PresentationMappingRule {


    @Override
    public void dtoToEntity(ModelMapper modelMapper) {
        modelMapper.createTypeMap(Location.class, LocationListingDTO.class)
        .addMappings(new PropertyMap<Location, LocationListingDTO>() {
            @Override
            protected void configure() {

            }
        });
    }

    @Override
    public void entityToDto(ModelMapper modelMapper) {

    }
}
