package caprita.catalin.cityquestbackend.controllers;

import caprita.catalin.cityquestbackend.controllers.dto.location.quest.UserQuestResponsesDto;
import caprita.catalin.cityquestbackend.controllers.dto.location.quest.UserSubtaskResponseDto;
import caprita.catalin.cityquestbackend.controllers.dto.user.*;
import caprita.catalin.cityquestbackend.domain.entities.UserSubtaskResponse;
import caprita.catalin.cityquestbackend.domain.entities.user.User;
import caprita.catalin.cityquestbackend.domain.entities.user.UserCompanion;
import caprita.catalin.cityquestbackend.domain.enums.QuestStatus;
import caprita.catalin.cityquestbackend.services.quest.QuestService;
import caprita.catalin.cityquestbackend.services.ratings.RatingService;
import caprita.catalin.cityquestbackend.services.user.UserAlreadyExistsException;
import caprita.catalin.cityquestbackend.services.user.UserService;
import caprita.catalin.cityquestbackend.util.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UserController.USER_API)
public class UserController {
    public static final String USER_API = Constants.Api.BASE_API_URL + "/users";
    private final UserService userService;
    private final QuestService questService;
    private final RatingService ratingService;
    private final ModelMapper modelMapper;



    @Autowired
    public UserController(UserService userService,
                          QuestService questService,
                          RatingService ratingService,
                          ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.questService = questService;
        this.ratingService = ratingService;
    }

    @PostConstruct
    public void addAdmin(){
        userService.createAdminUser();
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto createUserDTO){
        try{
            User created = userService.create(createUserDTO);
            UserDetailsDto dto = modelMapper.map(created, UserDetailsDto.class);
            dto.setEnabled(false);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dto);

        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (InternalError e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/register")
    public ResponseEntity<?> completeRegistrationWithDetails(@RequestBody UserRegisterDetailsDto dto){
        try{
            User updated = userService.completeRegister(dto);
            UserDetailsDto responseDto = modelMapper.map(updated, UserDetailsDto.class);
            responseDto.setEnabled(true);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseDto);
        }catch (UsernameNotFoundException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Constants.Error.COULD_NOT_LOCATE_USER);
        }catch (InternalError e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/register/rollback")
    public ResponseEntity<?> rollbackUserRegistration(@RequestBody UserRollbackDto dto){
        try {
            userService.rollbackUserRegistration(dto);
            return ResponseEntity.ok().build();
        } catch (InternalError e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedUserDetails(){
        try{
            UserDetailsDto dto = modelMapper.map(userService.getLoggedUser(), UserDetailsDto.class);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Constants.Error.COULD_NOT_LOCATE_USER);
        }
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDto dto){
        try{
            UserDetailsDto responseDto = modelMapper.map(userService.updateUser(dto), UserDetailsDto.class);
            return ResponseEntity.ok(responseDto);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Constants.Error.COULD_NOT_LOCATE_USER);
        }catch (InternalError e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
         }
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
            User user = userService.findById(id);
            if(user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Unknown user ID: %d", id));
            return ResponseEntity.ok(modelMapper.map(user, UserDetailsDto.class));
    }


    @PostMapping("/companions")
    public ResponseEntity<?> addCompanion(@RequestBody AddCompanionDto addCompanionDto){

        UserCompanion result = userService.addCompanion(addCompanionDto);
        if(result == null)
        return ResponseEntity.notFound().build();

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(modelMapper.map(result, UserCompanionDto.class));
    }

    @GetMapping("/me/quests")
    public ResponseEntity<?> getAllQuestsForUser(@RequestParam(name = "status") QuestStatus status){
        try {
            Long id = userService.getLoggedUser().getId();
            UserQuestsListingDto responseDto = questService.findAllBriefedByUserId(id, status);
            return ResponseEntity.ok(responseDto);
        } catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Constants.Error.COULD_NOT_LOCATE_USER);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InternalError e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

//    @GetMapping("/me/quests/{id}")
//    public ResponseEntity<?> getUserAnswersForQuest(@PathVariable("id") Long id){
//        try {
//            List<UserSubtaskResponse> responses = userService.getUserAnswersByQuestId(id);
//            UserQuestResponsesDto result = new UserQuestResponsesDto();
//            result.setQuestId(id);
//            result.setResponses(responses.stream()
//                    .map(usr -> modelMapper.map(usr, UserSubtaskResponseDto.class))
//                    .collect(Collectors.toList()));
//
//
//            return ResponseEntity.ok(result);
//        } catch (UsernameNotFoundException | NoSuchElementException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (InternalError e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }
}
