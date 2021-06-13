package caprita.catalin.cityquestbackend.services.user;

import caprita.catalin.cityquestbackend.controllers.dto.location.quest.UserQuestResponsesDto;
import caprita.catalin.cityquestbackend.controllers.dto.user.*;
import caprita.catalin.cityquestbackend.domain.entities.UserSubtaskResponse;
import caprita.catalin.cityquestbackend.domain.entities.user.User;
import caprita.catalin.cityquestbackend.domain.entities.user.UserCompanion;
import caprita.catalin.cityquestbackend.domain.enums.RewardType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

public interface UserService {
    User findById(Long userId);
    User findLazyForDtoById(Long id) throws UsernameNotFoundException;
    User findByUsername(String username) throws UsernameNotFoundException;
    User createFromFile(User user);
    User create(RegisterDto userDTO) throws UserAlreadyExistsException, InternalError;
    User completeRegister(UserRegisterDetailsDto dto) throws UsernameNotFoundException, InternalError;
    User updateUser(UpdateUserDto dto) throws UsernameNotFoundException, InternalError;
    User getLoggedUser() throws UsernameNotFoundException;
    UserCompanion addCompanion(AddCompanionDto dto) throws UsernameNotFoundException;
    void addTravelerTraits(List<RewardType> types, List<Integer> amounts) throws InternalError;
    void rollbackUserRegistration(UserRollbackDto dto) throws InternalError;
    void createAdminUser();
}
