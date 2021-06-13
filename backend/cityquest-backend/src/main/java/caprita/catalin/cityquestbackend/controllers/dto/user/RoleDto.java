package caprita.catalin.cityquestbackend.controllers.dto.user;

import caprita.catalin.cityquestbackend.domain.enums.RoleCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RoleDto implements Serializable {
    private RoleCode code;
    private String description;
}
