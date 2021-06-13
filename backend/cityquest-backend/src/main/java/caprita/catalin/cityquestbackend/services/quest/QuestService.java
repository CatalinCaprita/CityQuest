package caprita.catalin.cityquestbackend.services.quest;

import caprita.catalin.cityquestbackend.controllers.dto.location.quest.CreateQuestDto;
import caprita.catalin.cityquestbackend.controllers.dto.location.quest.QuestBriefDto;
import caprita.catalin.cityquestbackend.controllers.dto.location.quest.UserQuestResponsesDto;
import caprita.catalin.cityquestbackend.controllers.dto.location.quest.UserQuestResultDto;
import caprita.catalin.cityquestbackend.controllers.dto.user.UserQuestsListingDto;
import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import caprita.catalin.cityquestbackend.domain.enums.QuestStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.NoSuchElementException;

public interface QuestService {
    Quest findById(Long id) throws NoSuchElementException, InternalError;
    Quest findByIdWithSubtasks(Long id) throws  NoSuchElementException, InternalError;
    List<Quest> findAllByUserId(Long userId)throws  UsernameNotFoundException, InternalError;
    UserQuestsListingDto findAllBriefedByUserId(Long userId, QuestStatus status) throws  InternalError;
    Quest create(CreateQuestDto dto) throws NoSuchElementException, EntityExistsException, InternalError;
    UserQuestResultDto submitQuestResponse(UserQuestResponsesDto dto)  throws NoSuchElementException, InternalError;

}
