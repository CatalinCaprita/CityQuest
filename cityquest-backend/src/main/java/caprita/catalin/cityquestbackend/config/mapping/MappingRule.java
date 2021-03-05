package caprita.catalin.cityquestbackend.config.mapping;

import org.modelmapper.ModelMapper;

public interface MappingRule {
    void addMappings(ModelMapper modelMapper);
}
