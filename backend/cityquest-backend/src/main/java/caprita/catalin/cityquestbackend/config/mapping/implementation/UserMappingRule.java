package caprita.catalin.cityquestbackend.config.mapping.implementation;

import caprita.catalin.cityquestbackend.config.mapping.AuthMappingRule;
import caprita.catalin.cityquestbackend.config.mapping.PresentationMappingRule;
import caprita.catalin.cityquestbackend.controllers.dto.user.*;
import caprita.catalin.cityquestbackend.domain.entities.user.Role;
import caprita.catalin.cityquestbackend.domain.entities.user.User;
import caprita.catalin.cityquestbackend.domain.entities.user.UserCompanion;
import caprita.catalin.cityquestbackend.domain.enums.Gender;
import caprita.catalin.cityquestbackend.domain.enums.RoleCode;
import caprita.catalin.cityquestbackend.security.service.UserPrincipal;
import caprita.catalin.cityquestbackend.util.UserAnswerDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserMappingRule implements PresentationMappingRule, AuthMappingRule {

    private static final Set<Integer> recodedItems = new HashSet<>(Arrays.asList(
            2,6,8,9,12,18,21,23,24,27,31,34,35,37,41,43
    ));
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMappingRule.class);

    private static final Converter<Set<Role>, Set<RoleCode>> ROLE_TO_CODE = mappingContext -> {
        try {
            return mappingContext.getSource()
                    .stream()
                    .map(Role::getCode)
                    .collect(Collectors.toSet());
        }catch (Exception e) {
            LOGGER.error("Mapping ERROR: {}", e.getMessage());
            return null;
        }
    };
    private static final Converter<String, Gender> STRING_TO_GENDER = mappingContext -> {
        try{
            return Gender.valueOf(mappingContext.getSource().trim().toUpperCase(Locale.ROOT));
        }catch (Exception e){
            LOGGER.error("MAPPING ERROR: {}", e.getMessage());
            return Gender.MALE;
        }
    };

    private static final Converter<LocalDate, String> DATE_TO_STRING = mappingContext -> {
        return mappingContext.getSource().toString();
    };

    @Autowired
    public UserMappingRule() {
                recodedItems.forEach(item -> item --);
    }

    @Override
    public void dtoToEntity(ModelMapper modelMapper) {

        Converter<int[], double[]> personalityTestScoring = mappingContext -> {
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

        Converter<int[], double[]> tenBfiConverter = mappingContext -> {
            final double[] scores = new double[5];
            int[] responses = mappingContext.getSource();
            for(int i=0; i<=6; i+=2){
                responses[i] = 6 - responses[i];
            }
            responses[3] = 6 - responses[3];
//            Extraversion
            scores[0] = 0.5 * (responses[0] + responses[4]);
//              Agreeableness
            scores[1] = 0.5 * (responses[1] + responses[6]);
//            Conscienciousness
            scores[2] = 0.5 * (responses[2] + responses[7]);
//            Neuroticism
            scores[3] = 0.5 * (responses[3] + responses[8]);
//            OTE
            scores[4] = 0.5 * (responses[4] + responses[9]);
            return scores;
        };

        modelMapper.createTypeMap(UserAnswerDto.class, User.class)
                .addMappings(new PropertyMap<UserAnswerDto, User>() {
                    @Override
                    protected void configure() {
                        map(source.getTimestamp(),destination.getUsername());
                        using(personalityTestScoring).map(source.getPersonalityTest(),destination.getScores());
                    }
                });

        modelMapper.createTypeMap(UserRegisterDetailsDto.class, User.class)
                .addMappings(new PropertyMap<>() {
                    @Override
                    protected void configure() {
                        skip(source.getId(), destination.getId());
                        skip(source.getCompanions(), destination.getCompanions());
                        using(tenBfiConverter).map(source.getQuizResponses(), destination.getScores());
                        using(STRING_TO_GENDER).map(source.getGender(), destination.getGender());
                    }
                });

        modelMapper.createTypeMap(AddCompanionDto.class, UserCompanion.class)
                .addMappings(new PropertyMap<AddCompanionDto, UserCompanion>() {
                    @Override
                    protected void configure() {

                    }
                });
        modelMapper.createTypeMap(UpdateUserDto.class, User.class)
                .addMappings(new PropertyMap<UpdateUserDto, User>() {
                    @Override
                    protected void configure() {
                    }
                });

    }

    @Override
    public void entityToDto(ModelMapper modelMapper) {
        modelMapper.createTypeMap(Role.class, RoleDto.class)
                .addMappings(new PropertyMap<Role, RoleDto>() {
                    @Override
                    protected void configure() {

                    }
                });
        modelMapper.createTypeMap(UserCompanion.class, UserCompanionDto.class)
                .addMappings(new PropertyMap<>() {
                    @Override
                    protected void configure() {

                    }
                });

        modelMapper.createTypeMap(User.class, UserDetailsDto.class)
                .addMappings(new PropertyMap<User, UserDetailsDto>() {
                    @Override
                    protected void configure() {
                        using(DATE_TO_STRING).map(source.getJoinDate(), destination.getJoinDate());
                    }
                });
    }

    @Override
    public void entityToPrincipal(ModelMapper modelMapper) {
        modelMapper.createTypeMap(User.class, UserPrincipal.class)
                .addMappings(new PropertyMap<User, UserPrincipal>() {
                    @Override
                    protected void configure() {
                        map(source.getUsername(), destination.getUsername());
                        map(source.getPassword(), destination.getPassword());
                    }
                });
    }

}
