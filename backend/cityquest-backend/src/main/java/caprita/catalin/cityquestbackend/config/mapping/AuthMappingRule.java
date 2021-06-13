package caprita.catalin.cityquestbackend.config.mapping;

import org.modelmapper.ModelMapper;

public interface AuthMappingRule extends MappingRule{
    void entityToPrincipal(ModelMapper modelMapper);
}
