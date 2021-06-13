package caprita.catalin.cityquestbackend.controllers;

import caprita.catalin.cityquestbackend.controllers.dto.location.quest.CreateQuestDto;
import caprita.catalin.cityquestbackend.controllers.dto.location.quest.QuestDetailedDto;
import caprita.catalin.cityquestbackend.controllers.dto.location.quest.UserQuestResponsesDto;
import caprita.catalin.cityquestbackend.controllers.dto.location.quest.UserQuestResultDto;
import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import caprita.catalin.cityquestbackend.domain.enums.RewardType;
import caprita.catalin.cityquestbackend.services.quest.QuestService;
import caprita.catalin.cityquestbackend.services.location.LocationService;
import caprita.catalin.cityquestbackend.services.user.UserService;
import caprita.catalin.cityquestbackend.util.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(QuestController.QUESTS_URL)
public class QuestController {
    public static final String QUESTS_URL = Constants.Api.BASE_API_URL + "/quests";
    private final QuestService questService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public QuestController(QuestService questService,
                           UserService userService,
                           ModelMapper modelMapper) {
        this.questService = questService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findQuestById(@PathVariable("id") Long id){
        try {
            Quest q = questService.findByIdWithSubtasks(id);
            QuestDetailedDto responseDto = modelMapper.map(q, QuestDetailedDto.class);
            return ResponseEntity.ok(responseDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    String.format(Constants.Error.COULD_NOT_LOCATE_QUEST, id));
        } catch (InternalError e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> submitQuestResponseForUser(@PathVariable("id") Long questId,
                                                        @RequestBody UserQuestResponsesDto dto){
        try {
                Long userId = userService.getLoggedUser().getId();
                dto.setUserId(userId);
                dto.setQuestId(questId);
                UserQuestResultDto result = questService.submitQuestResponse(dto);
                userService.addTravelerTraits(
                        Arrays.asList(RewardType.valueOf(result.getPrimaryRewardType()),
                                        RewardType.valueOf(result.getSecondaryRewardType())),
                        Arrays.asList(result.getPrimaryRewardAmount(), result.getSecondaryRewardAmount())
                        );
            return ResponseEntity.ok(result);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Constants.Error.COULD_NOT_LOCATE_USER);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InternalError e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createQuest(@RequestBody CreateQuestDto dto){
        try {
            Quest q = questService.create(dto);
            QuestDetailedDto responseDto = modelMapper.map(q, QuestDetailedDto.class);
            return ResponseEntity.ok(responseDto);

        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    String.format(Constants.Error.COULD_NOT_LOCATE_LOCATION, dto.getLocationId()));
        }catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Constants.Error.QUEST_EXISTS);
        } catch (InternalError e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}