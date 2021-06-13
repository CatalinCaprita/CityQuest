package caprita.catalin.cityquestbackend.config.mapping.implementation;

import caprita.catalin.cityquestbackend.config.mapping.PresentationMappingRule;
import caprita.catalin.cityquestbackend.controllers.dto.location.quest.*;
import caprita.catalin.cityquestbackend.domain.entities.UserSubtaskResponse;
import caprita.catalin.cityquestbackend.domain.entities.quest.PossibleAnswer;
import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import caprita.catalin.cityquestbackend.domain.entities.quest.Subtask;
import caprita.catalin.cityquestbackend.domain.enums.QuestType;
import caprita.catalin.cityquestbackend.domain.enums.RewardType;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class QuestMappingRule implements PresentationMappingRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestMappingRule.class);
    private static final Converter<String, QuestType> STR_TO_QTYPE = mappingContext -> {
      try{
          return QuestType.valueOf(mappingContext.getSource());
      }catch (Exception e){
            LOGGER.error("MAPPING EXCPETION {}", e.getMessage());
            return QuestType.QUIZ;
      }
    };
    private static final Converter<String, RewardType> STR_TO_RTYPE = mappingContext -> {
        try{
            return RewardType.valueOf(mappingContext.getSource());
        }catch (Exception e){
            LOGGER.error("MAPPING EXCPETION {}", e.getMessage());
            return RewardType.KNOWLEDGE;
        }
    };
    private static final Converter<RewardType, String> RWD_TO_STRING = mappingContext -> {
        return mappingContext.getSource().toString();
    };
    private static final Converter<QuestType, String> TYPE_TO_STRING = mappingContext -> {
        return mappingContext.getSource().toString();
    };


    @Override
    public void dtoToEntity(ModelMapper modelMapper) {
        modelMapper.createTypeMap(PossibleAnswerDto.class, PossibleAnswer.class)
                .addMappings(new PropertyMap<>() {
                    @Override
                    protected void configure() {
                        skip(destination.getSubtask());
                    }
                });
        modelMapper.createTypeMap(CreateSubtaskDto.class, Subtask.class)
                .addMappings(new PropertyMap<CreateSubtaskDto, Subtask>() {
                    @Override
                    protected void configure() {
                        skip(destination.getPossibleAnswers());
                    }
                });
        modelMapper.createTypeMap(CreateQuestDto.class, Quest.class)
                .addMappings(new PropertyMap<CreateQuestDto, Quest>() {
                    @Override
                    protected void configure() {
                        using(STR_TO_QTYPE).map(source.getType(), destination.getType());
                        using(STR_TO_RTYPE).map(source.getPrimaryRewardType(), destination.getPrimaryRewardType());
                        using(STR_TO_RTYPE).map(source.getSecondaryRewardType(), destination.getSecondaryRewardType());
                        skip(destination.getSubtasks());
                    }
                });

    }

    @Override
    public void entityToDto(ModelMapper modelMapper) {
        modelMapper.createTypeMap(PossibleAnswer.class, PossibleAnswerDto.class)
                .addMappings(new PropertyMap<>() {
                    @Override
                    protected void configure() {

                    }
                });
        modelMapper.createTypeMap(Subtask.class, SubtaskDto.class)
                .addMappings(new PropertyMap<Subtask, SubtaskDto>() {
                    @Override
                    protected void configure() {

                    }
                });
        modelMapper.createTypeMap(Quest.class, QuestDetailedDto.class)
                .addMappings(new PropertyMap<>() {
                    @Override
                    protected void configure() {
                        map(source.getPrimaryRewardType(), destination.getPrimaryRewardType());
                        map(source.getSecondaryRewardType(), destination.getSecondaryRewardType());
                        map(source.getType(), destination.getType());
                    }
                });
        modelMapper.createTypeMap(Quest.class, QuestBriefDto.class)
                .addMappings(new PropertyMap<>() {
                    @Override
                    protected void configure() {
                        using(TYPE_TO_STRING).map(source.getType(),destination.getType());
                        using(RWD_TO_STRING).map(source.getPrimaryRewardType(),destination.getPrimaryRewardType());
                        using(RWD_TO_STRING).map(source.getSecondaryRewardType(),destination.getSecondaryRewardType());

                    }
                });

    }
}
