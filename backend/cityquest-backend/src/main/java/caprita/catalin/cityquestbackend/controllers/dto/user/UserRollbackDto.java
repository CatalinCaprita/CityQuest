package caprita.catalin.cityquestbackend.controllers.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserRollbackDto implements Serializable {
    private Long userId;
    private String username;
}
