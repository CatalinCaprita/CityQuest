package caprita.catalin.cityquestbackend.controllers.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LoginSuccessDto implements Serializable {
    private String username;
    private String token;
    private Long id;
}
