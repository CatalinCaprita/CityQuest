package caprita.catalin.cityquestbackend.config.mapping;

import org.dom4j.rule.Mode;
import org.modelmapper.ModelMapper;

public interface PresentationMappingRule extends MappingRule{
    @Override
    default void addMappings(ModelMapper modelMapper){
        dtoToEntity(modelMapper);
        entityToDto(modelMapper);
    }

     void dtoToEntity(ModelMapper modelMapper);
     void entityToDto(ModelMapper modelMapper);
}
