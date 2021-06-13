package caprita.catalin.cityquestbackend.services.quest;

import caprita.catalin.cityquestbackend.controllers.dto.location.quest.*;
import caprita.catalin.cityquestbackend.controllers.dto.user.UserQuestsListingDto;
import caprita.catalin.cityquestbackend.domain.entities.*;
import caprita.catalin.cityquestbackend.domain.entities.quest.PossibleAnswer;
import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import caprita.catalin.cityquestbackend.domain.entities.quest.Subtask;
import caprita.catalin.cityquestbackend.domain.enums.QuestStatus;
import caprita.catalin.cityquestbackend.domain.enums.QuestType;
import caprita.catalin.cityquestbackend.repositories.location.LocationRepository;
import caprita.catalin.cityquestbackend.repositories.quest.QuestRepository;
import caprita.catalin.cityquestbackend.repositories.quest.SubtaskRepository;
import caprita.catalin.cityquestbackend.repositories.user_quest.UserQuestLogRepository;
import caprita.catalin.cityquestbackend.repositories.user_quest.UserSubtaskResponseRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestServiceImpl implements QuestService{
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestService.class);
    private final SubtaskRepository subtaskRepository;
    private final QuestRepository questRepository;
    private final LocationRepository locationRepository;
    private final UserQuestLogRepository userQuestLogRepository;
    private final UserSubtaskResponseRepository userSubtaskResponseRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public QuestServiceImpl(SubtaskRepository subtaskRepository,
                            QuestRepository questRepository,
                            LocationRepository locationRepository,
                            UserQuestLogRepository userQuestLogRepository,
                            UserSubtaskResponseRepository userSubtaskResponseRepository,
                            ModelMapper modelMapper) {
        this.questRepository = questRepository;
        this.modelMapper = modelMapper;
        this.locationRepository = locationRepository;
        this.subtaskRepository = subtaskRepository;
        this.userQuestLogRepository = userQuestLogRepository;
        this.userSubtaskResponseRepository = userSubtaskResponseRepository;
    }

    @Override
    public Quest findById(Long id) throws NoSuchElementException, InternalError {
        return null;
    }

    @Override
    public Quest findByIdWithSubtasks(Long id) throws NoSuchElementException, InternalError {
        return questRepository.findWithSubtasksById(id).orElseThrow();
    }

    @Override
    public List<Quest> findAllByUserId(Long userId) throws UsernameNotFoundException, InternalError {
        try{
            return questRepository.findAllByUserIdAndStatus(userId, QuestStatus.REMAINING);
        }catch (Exception e){
            throw new InternalError(e.getMessage());
        }
    }

    @Override
    public UserQuestsListingDto findAllBriefedByUserId(Long userId, QuestStatus status) throws InternalError {
        try{
            List<Quest> allByUserId = questRepository.findAllByUserIdAndStatus(userId, status);
            List<QuestBriefDto> briefings = allByUserId.stream()
                    .map(quest -> {
                        QuestBriefDto dto = modelMapper.map(quest, QuestBriefDto.class);
                        UserQuestLogKey uqlKey = new UserQuestLogKey(userId, quest.getId());
                        UserQuestLog uql = userQuestLogRepository.findById(uqlKey).orElseThrow();
                        if(uql.getCompletionDate() != null)
                            dto.setCompletionDate(uql.getCompletionDate().toString());
                        dto.setLocationName(quest.getLocationName());
                        return dto;
                    }).collect(Collectors.toList());

            switch (status){
                case FINISHED:
                    return buildFinisedhQuests(briefings);
                case REMAINING:
                    return buildRemainingQuests(briefings);
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }
    }



    @Override
    public Quest create(CreateQuestDto dto) throws NoSuchElementException, EntityExistsException, InternalError {
        try{
            questRepository.findById(dto.getLocationId()).ifPresent( q -> {throw new EntityExistsException();});

            Location location = locationRepository.findById(dto.getLocationId()).orElseThrow();

            Quest newQuest = new Quest();
            newQuest.setLocation(location);
            newQuest.setLocationName(location.getName());
            modelMapper.map(dto, newQuest);

//            final Quest persistedQuest = questRepository.save(newQuest);
//            final Quest finalQuest = newQuest;
            dto.getSubtasks().forEach(createSubtaskDto -> {
                final Subtask st = modelMapper.map(createSubtaskDto, Subtask.class);
                createSubtaskDto.getPossibleAnswers().forEach(possibleAnswer -> {
                    final PossibleAnswer pa = modelMapper.map(possibleAnswer, PossibleAnswer.class);
                    st.addPossibleAnswer(pa);
                });
//                st.setQuest(persistedQuest);
//                subtaskRepository.save(st);
                newQuest.addSubtask(st);
            });
            return questRepository.save(newQuest);
        }catch (EntityExistsException e){
            LOGGER.error("Quest Already exists for location {}", dto.getLocationId());
            throw new EntityExistsException(dto.getLocationId().toString());
        }catch (NoSuchElementException e){

            LOGGER.error("Location or quest not found {}", dto.getLocationId());
            throw e;
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("Persistance Exception {}", e.getMessage());
            throw new InternalError();
        }
    }

    @Override
    public UserQuestResultDto submitQuestResponse(UserQuestResponsesDto dto) throws UsernameNotFoundException, NoSuchElementException, InternalError {
        try {
            Quest quest = questRepository.findWithSubtasksById(dto.getQuestId())
                    .orElseThrow();
            List<UserSubtaskResultDto> subtaskResults = new ArrayList<>();
            dto.getResponses().forEach(userSubtaskResponseDto -> {
//               For each response, look up the subtask
                quest.getSubtasks()
                        .stream()
                        .filter(subtask -> subtask.getId().equals(userSubtaskResponseDto.getSubtaskId()))
                        .findFirst()
                        .ifPresent(subtask -> {
                            final UserSubtaskResultDto resultDto;
                            switch (quest.getType()){
                                case QUIZ:
                                    resultDto = buildDtoFromQuiz(subtask, userSubtaskResponseDto);
                                    break;
                                case STROLL_AND_SEE:
                                    resultDto = buildDtoFromSns(subtask, userSubtaskResponseDto);
                                    break;
                                default:
                                    resultDto = buildDtoFromGuess(subtask, userSubtaskResponseDto);
                                    break;
                            }
                            UserSubtaskResponseKey id = new UserSubtaskResponseKey(dto.getUserId(),
                                    subtask.getId(),
                                    quest.getId());
                            UserSubtaskResponse response = userSubtaskResponseRepository.findById(id).orElse(null);
                            if(response == null) {
                                response = new UserSubtaskResponse();
                                response.setId(id);
                            }
                            response.setCorrect(resultDto.getUserCorrect());
                            userSubtaskResponseRepository.save(response);
                            subtaskResults.add(resultDto);

                        });
            });
            long totalSubstasksCorrect = subtaskResults.stream()
                    .filter(UserSubtaskResultDto::getUserCorrect)
                    .count();
            UserQuestResultDto resultDto = new UserQuestResultDto();
            resultDto.setResults(subtaskResults);
            resultDto.setQuestId(quest.getId());
            resultDto.setPrimaryRewardAmount((int)(quest.getPrimaryRewardAmount() / quest.getSubtasks().size() * totalSubstasksCorrect));
            resultDto.setSecondaryRewardAmount((int)(quest.getSecondaryRewardAmount() / quest.getSubtasks().size() * totalSubstasksCorrect));
            resultDto.setPrimaryRewardType(quest.getPrimaryRewardType().toString());
            resultDto.setSecondaryRewardType(quest.getSecondaryRewardType().toString());

            UserQuestLogKey key = new UserQuestLogKey(dto.getUserId(), quest.getId());
            UserQuestLog log = userQuestLogRepository.findById(key).orElse(null);
            if(log == null) {
                log = new UserQuestLog();
                log.setId(key);
            }
            log.setCompletionDate(LocalDate.parse(dto.getCompletionDate()));
            log.setProgress(QuestStatus.FINISHED);
            userQuestLogRepository.save(log);

            return resultDto;
        }catch (NoSuchElementException e ){
            e.printStackTrace();
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }
    }

    private UserSubtaskResultDto buildDtoFromGuess(Subtask subtask, UserSubtaskResponseDto responseDto) {
        UserSubtaskResultDto resultDto = new UserSubtaskResultDto();
        resultDto.setSubtaskContent(subtask.getDescription());
        resultDto.setSubtaskId(subtask.getId());
        final boolean userGuessed = !responseDto.getUserAnswerId().equals(-1L);
        PossibleAnswer uniqueAnswer = subtask.getPossibleAnswers().get(0);
        resultDto.setCorrectAnswerId(uniqueAnswer.getId());
        resultDto.setCorrectAnswerContent(uniqueAnswer.getContent());
        if(!userGuessed){
            resultDto.setUserCorrect(false);
            resultDto.setUserAnswerContent(null);
            resultDto.setUserAnswerId(-1L);
            return resultDto;
        }
//        If user did make  a guess then we must compare it
        String [] rangeValues = uniqueAnswer.getContent().split("-");
        int min = Integer.parseInt(rangeValues[0]);
        int max = Integer.parseInt(rangeValues[1]);
        String [] userRangeValues = responseDto.getUserAnswerValue().split("-");
        int umin = Integer.parseInt(userRangeValues[0]);
        int umax = Integer.parseInt(userRangeValues[1]);

        resultDto.setUserAnswerContent(responseDto.getUserAnswerValue());
        resultDto.setUserAnswerId(uniqueAnswer.getId());
        resultDto.setUserCorrect( min == umin && max == umax);
        return  resultDto;
    }

    private UserSubtaskResultDto buildDtoFromSns(Subtask subtask, UserSubtaskResponseDto responseDto) {
        UserSubtaskResultDto resultDto = new UserSubtaskResultDto();
        resultDto.setSubtaskContent(subtask.getDescription());
        resultDto.setSubtaskId(subtask.getId());
        final boolean userChoseItem = !responseDto.getUserAnswerId().equals(-1L);
        PossibleAnswer uniqueAnswer = subtask.getPossibleAnswers().get(0);
        if(uniqueAnswer.getIsCorrect()){
            resultDto.setCorrectAnswerContent(uniqueAnswer.getContent());
            resultDto.setCorrectAnswerId(uniqueAnswer.getId());
//            We at leas know the correct answer fields
            if(userChoseItem) {
//                So If user chose item, he must be correct as well
                resultDto.setUserAnswerContent(uniqueAnswer.getContent());
                resultDto.setUserAnswerId(uniqueAnswer.getId());
                resultDto.setUserCorrect(true);
            }else{
//                So if the user did not choose the item his answer is wrong
                resultDto.setUserAnswerContent(null);
                resultDto.setUserAnswerId(-1L);
                resultDto.setUserCorrect(false);
            }
        }else{
            resultDto.setCorrectAnswerContent(uniqueAnswer.getContent());
            resultDto.setCorrectAnswerId(uniqueAnswer.getId());
//            So if item was NOT correct
            if(userChoseItem) {
//                So If user chose item, he chose wrong,
                resultDto.setUserAnswerContent(uniqueAnswer.getContent());
                resultDto.setUserAnswerId(uniqueAnswer.getId());
                resultDto.setUserCorrect(false);
            }else{
//                So if the user did not choose the item his answer is correct. Because it was a wrong one
                resultDto.setUserAnswerContent(null);
                resultDto.setUserAnswerId(-1L);
                resultDto.setUserCorrect(true);
            }
        }
        return resultDto;
    }

    private UserSubtaskResultDto buildDtoFromQuiz(Subtask subtask,
                                                  UserSubtaskResponseDto responseDto) {
        UserSubtaskResultDto resultDto = new UserSubtaskResultDto();
        resultDto.setSubtaskContent(subtask.getDescription());
        resultDto.setSubtaskId(subtask.getId());
        final boolean userAnswered = !responseDto.getUserAnswerId().equals(-1L);

        subtask.getPossibleAnswers()
                .forEach(possibleAnswer -> {
                    if(possibleAnswer.getIsCorrect()) {
                        resultDto.setCorrectAnswerId(possibleAnswer.getId());
                        resultDto.setCorrectAnswerContent(possibleAnswer.getContent());
                    }
                    if(userAnswered &&
                            possibleAnswer.getId().equals(responseDto.getUserAnswerId())){
                        resultDto.setUserAnswerId(possibleAnswer.getId());
                        resultDto.setUserAnswerContent(possibleAnswer.getContent());
                        resultDto.setUserCorrect(possibleAnswer.getIsCorrect());
                    }
                });
        if(!userAnswered){
            resultDto.setUserAnswerId(-1L);
            resultDto.setUserAnswerContent(null);
            resultDto.setUserCorrect(false);
        }
        return resultDto;
    }


    private UserQuestsListingDto buildRemainingQuests(List<QuestBriefDto> briefings) {
        Map<Integer, List<QuestBriefDto>> questsResponse = new HashMap<>();
        questsResponse.put(0, briefings);
        UserQuestsListingDto dto = new UserQuestsListingDto();
        dto.setQuestsStatus(QuestStatus.REMAINING.toString());
        dto.setQuestsByDay(questsResponse);
        return dto;
    }
    private UserQuestsListingDto buildFinisedhQuests(List<QuestBriefDto> briefings){

        Map<LocalDate, List<QuestBriefDto>> filteredByDate  = new HashMap<>();
        List<LocalDate> dateToInteger = new ArrayList<>();

        briefings.forEach(questBriefModel -> {
            LocalDate date = LocalDate.parse(questBriefModel.getCompletionDate());
            if(!filteredByDate.containsKey(date)){
                filteredByDate.put(date, new ArrayList<>());
            }
            filteredByDate.get(date).add(questBriefModel);
        });

        filteredByDate.keySet()
                .stream()
                .sorted()
                .forEach(dateToInteger::add);
//             index i == LocalDate ith to the present, so dateToInteger[0] will be Key for Quests in day 0
        UserQuestsListingDto responseDto = new UserQuestsListingDto();
        Map<Integer, List<QuestBriefDto>> questByDay = new HashMap<>();
        for(int i=0 ; i < dateToInteger.size(); i++){
            questByDay.put(i, filteredByDate.get(dateToInteger.get(i)));
        }
//            If there are quests completed in less than 2 days, add emptyLists up to 3
        for(int i=dateToInteger.size() ; i < 3; i++){
            questByDay.put(i, Collections.emptyList());
        }
        responseDto.setQuestsByDay(questByDay);
        responseDto.setQuestsStatus(QuestStatus.FINISHED.toString());
        return responseDto;
    }

}
