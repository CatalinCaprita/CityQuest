package caprita.catalin.cityquestbackend.config.mapping.user;

import caprita.catalin.cityquestbackend.config.mapping.PresentationMappingRule;
import caprita.catalin.cityquestbackend.domain.entities.User;
import caprita.catalin.cityquestbackend.util.UserAnswerDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class UserMappingRule implements PresentationMappingRule {

    private static final Set<Integer> recodedItems = new HashSet<>(Arrays.asList(
            2,6,8,9,12,18,21,23,24,27,31,34,35,37,41,43
    ));
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMappingRule.class);
    public UserMappingRule() {
        recodedItems.forEach(item -> item --);
    }

    @Override
    public void dtoToEntity(ModelMapper modelMapper) {

        Converter<int[], double[]> personalityTestScoring = mappingContext -> {
                User user = new User();
                double[] bfiScores = new double[5];
                int[] source = mappingContext.getSource();
                for(int i= 0 ; i < mappingContext.getSource().length; i++){
                    if (recodedItems.contains(i))
                        source[i] = 6 - source[i];
                }
                for(int i=0; i<source.length; i++){
                    switch (i%5){
                        case 0:{ if( i != 40) bfiScores[i%5] += source[i] / 8.0;break;}
                        case 1:{bfiScores[i%5] += source[i] / 9.0;break;}
                        case 2:{bfiScores[i%5] += source[i] / 9.0;break;}
                        case 3:{if (i != 43) bfiScores[i%5] += source[i] / 8.0;break;}
                        case 4:{bfiScores[i%5] += source[i] / 10.0;break;}
                    }

                }
                bfiScores[4] += 1/10.0 * (source[40]) + 1/10.0 * (source[43]);

            return bfiScores;
            };

        modelMapper.createTypeMap(UserAnswerDto.class, User.class)
                .addMappings(new PropertyMap<UserAnswerDto, User>() {
                    @Override
                    protected void configure() {
                        map(source.getTimestamp(),destination.getUsername());
                        using(personalityTestScoring).map(source.getPersonalityTest(),destination.getScores());
                    }
                });
    }

    @Override
    public void entityToDto(ModelMapper modelMapper) {

    }
}
