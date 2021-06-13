package caprita.catalin.cityquestbackend.controllers.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
/**
 * A class that encapsulates the request of creating a new user with the given information.
 * and persisting it in the database*/
public class RegisterDto implements Serializable {
    private String username;
    private String password;
    private String email;
    /*Other fields omitted for brevity*/
}
