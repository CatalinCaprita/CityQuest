package caprita.catalin.cityquestbackend.config.mapping;

import org.dom4j.rule.Mode;
import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MappingConfig implements WebMvcConfigurer {
    private final List<MappingRule> mappingRules;

    @Autowired
    public MappingConfig(List<MappingRule> mappingRules) {
        this.mappingRules = mappingRules;
    }

    @Bean
    @Primary
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setImplicitMappingEnabled(true)
                .setFieldMatchingEnabled(true)
                .setFullTypeMatchingRequired(true)
                .setSkipNullEnabled(true)
                .setPropertyCondition(Conditions.isNotNull())
                .setMatchingStrategy(MatchingStrategies.STRICT);
        mappingRules.forEach(mappingRule -> mappingRule.addMappings(modelMapper));
        return  modelMapper;
    }
}
