package caprita.catalin.cityquestbackend.controllers.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
public class UserCompanionDto {
    private Long id;
    private String name;
    private String nickname;

}
