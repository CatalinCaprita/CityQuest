package caprita.catalin.cityquestbackend.controllers.dto.user;

import caprita.catalin.cityquestbackend.domain.entities.user.UserCompanion;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRegisterDetailsDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String gender;

    private int[] quizResponses;

    private List<UserCompanion> companions;

}