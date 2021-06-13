package caprita.catalin.cityquestbackend.services.user;

import caprita.catalin.cityquestbackend.controllers.dto.user.*;
import caprita.catalin.cityquestbackend.domain.entities.user.User;
import caprita.catalin.cityquestbackend.domain.entities.user.UserCompanion;
import caprita.catalin.cityquestbackend.domain.enums.RewardType;
import caprita.catalin.cityquestbackend.domain.enums.RoleCode;
import caprita.catalin.cityquestbackend.repositories.user.UserLocationRatingRepository;
import caprita.catalin.cityquestbackend.repositories.user_quest.UserQuestLogRepository;
import caprita.catalin.cityquestbackend.repositories.user_quest.UserSubtaskResponseRepository;
import caprita.catalin.cityquestbackend.repositories.user.RoleRepository;
import caprita.catalin.cityquestbackend.repositories.user.UserCompanionRepository;
import caprita.catalin.cityquestbackend.repositories.user.UserRepository;
import caprita.catalin.cityquestbackend.util.Constants;
import javassist.bytecode.ConstantAttribute;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserCompanionRepository userCompanionRepository;
    private final RoleRepository roleRepository;
    private final UserSubtaskResponseRepository userSubtaskResponseRepository;
    private  final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_USERNAME}")
    private String adminUser;
    @Value("${ADMIN_PASSWORD}")
    private String adminPass;


    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserCompanionRepository userCompanionRepository,
                           RoleRepository roleRepository,
                           UserSubtaskResponseRepository userSubtaskResponseRepository,
                           UserQuestLogRepository userQuestLogRepository,
                           UserLocationRatingRepository userLocationRatingRepository,
                           ModelMapper modelMapper,
                           @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userCompanionRepository = userCompanionRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userSubtaskResponseRepository = userSubtaskResponseRepository;
    }
    @Override
    public void createAdminUser(){
        try{
            userRepository.findByUsername(adminUser)
                    .orElseThrow(() -> new UsernameNotFoundException(Constants.Error.COULD_NOT_LOCATE_USER));

        }catch (UsernameNotFoundException e){
            LOGGER.debug("Did not initalize the admin user. Initializing");
            User admin = new User();
            admin.setEmail("admin@admin.com");
            admin.setPassword(passwordEncoder.encode(adminPass));
            admin.setUsername(adminUser);
            userRepository.save(admin);
        }
    }
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public User create(RegisterDto registerDto) throws UserAlreadyExistsException, NoSuchElementException, PersistenceException {
        try {
            User lookup = userRepository.findByUsernameOrEmail(
                registerDto.getUsername(),
                registerDto.getEmail()).stream().findAny().orElse(null);
        if(lookup != null)
            throw new UserAlreadyExistsException();
        User newUser = new User();
        newUser.setUsername(registerDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        newUser.setEmail(registerDto.getEmail());
        newUser.getRoles().add(roleRepository.findByCode(RoleCode.USER).orElseThrow());

            return userRepository.save(newUser);
        }catch (UserAlreadyExistsException | IncorrectResultSizeDataAccessException | NonUniqueResultException e){
            throw new UserAlreadyExistsException(Constants.Error.USER_CREDS_TAKEN);
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalError(Constants.Error.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public User completeRegister(UserRegisterDetailsDto dto) throws UsernameNotFoundException, InternalError{
        try{
            User toUpdate = userRepository.findById(dto.getId()).orElseThrow();
            modelMapper.map(dto, toUpdate);
            if(dto.getCompanions() != null  && !dto.getCompanions().isEmpty())
                dto.getCompanions().forEach(userCompanion -> {
                    userCompanion.setUser(toUpdate);
                    try{
                        userCompanionRepository.save(userCompanion);
                    }catch (Exception e){
                        LOGGER.error("COMPANION PERSISTENCE FAILED: {}", e.getMessage());
                    }

                });
            return userRepository.save(toUpdate);
        } catch(NoSuchElementException e){
            LOGGER.error("Persistence error: {}",e.getMessage());
            throw new UsernameNotFoundException(String.format(Constants.Error.COULD_NOT_LOCATE_USER, dto.getId()));
        } catch (Exception e){
            e.printStackTrace();
            LOGGER.error("Persistence error: {}",e.getMessage());
            throw new InternalError(Constants.Error.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public User updateUser(UpdateUserDto dto) throws UsernameNotFoundException, InternalError {
        try{
            User user = getLoggedUser();
            modelMapper.map(dto, user);
            return userRepository.save(user);
        }catch (UsernameNotFoundException ex){
            throw ex;
        }
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User findLazyForDtoById(Long id) throws UsernameNotFoundException {
        return userRepository.findLazyForDetailsDtoById(id)
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(Constants.Error.COULD_NOT_LOCATE_USER, id)));
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElse(null);
    }


    @Override
    public User createFromFile(User user) {
        User exists =  userRepository.findByUsername(user.getUsername()).orElse(null);
        if(exists == null){
            LOGGER.info("Persisted NEW User with timeStamp {}. Id {} ", user.getUsername(),
                    user.getId());
            return userRepository.save(user);
        }
        LOGGER.debug("Persisted EXISTENT User with timeStamp {}. Id {} ", user.getUsername(),
                user.getId());
        user.setId(exists.getId());
        return  userRepository.save(user);

    }

    @Override
    public User getLoggedUser() throws UsernameNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = (String) auth.getPrincipal();
        User user =  userRepository.findByUsername(principal)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(String.format(Constants.Error.COULD_NOT_LOCATE_USER, principal)));
        if(user.getJoinDate() == null){
            user.setJoinDate(LocalDate.now());
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public UserCompanion addCompanion(AddCompanionDto dto) throws UsernameNotFoundException{
        try{
            User current = getLoggedUser();
            UserCompanion companion = modelMapper.map(dto, UserCompanion.class);
            companion.setUser(current);
            return userCompanionRepository.save(companion);

        }catch (UsernameNotFoundException e){
            e.printStackTrace();
            throw e;
        }catch (Exception e){
            throw new UsernameNotFoundException(String.format(Constants.Error.COULD_NOT_LOCATE_USER,""));
        }
    }

    @Override
    public void addTravelerTraits(List<RewardType> types, List<Integer> amounts) throws InternalError{
        try {
                User u = getLoggedUser();
                for (int i=0 ; i < types.size(); i++) {
                    RewardType rtype = types.get(i);
                    int amount = amounts.get(i);
                    switch (rtype) {
                        case VITALITY:
                            u.setVitality(u.getVitality() + amount);
                            break;
                        case KNOWLEDGE:
                            u.setKnowledge(u.getKnowledge() + amount);
                            break;
                        case SWIFTNESS:
                            u.setSwiftness(u.getSwiftness() + amount);
                            break;
                        case SOCIABILITY:
                            u.setSociability(u.getSociability() + amount);
                            break;
                    }
                }
                userRepository.save(u);
        }catch (UsernameNotFoundException e) {
            e.printStackTrace();
            throw e;
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }
    }

    @Override
    public void rollbackUserRegistration(UserRollbackDto dto) throws InternalError {
        try{
            User u = userRepository.findByUsername(dto.getUsername()).orElseThrow();
            userRepository.delete(u);
        }catch (NoSuchElementException e){
            LOGGER.error("Tried to erase a user that was not persisted yet.");
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }
    }

//    @Override
//    public List<UserSubtaskResponse> getUserAnswersByQuestId(Long questId) throws UsernameNotFoundException, NoSuchElementException, InternalError {
//       try{
//           Long uid = getLoggedUser().getId();
//           questRepository.findById(questId).orElseThrow();
//           return userSubtaskResponseRepository.findAllByUser_IdAndQuest_Id(uid, questId);
//       }catch (NoSuchElementException e){
//           throw new NoSuchElementException(
//                   String.format(Constants.Error.COULD_NOT_LOCATE_QUEST, questId.toString()));
//       }catch (UsernameNotFoundException e){
//           throw e;
//       }catch (Exception e){
//           throw new InternalError(e.getMessage());
//       }
//    }

}
