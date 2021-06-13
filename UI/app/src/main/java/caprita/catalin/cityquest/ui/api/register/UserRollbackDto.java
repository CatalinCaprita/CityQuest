package caprita.catalin.cityquest.ui.api.register;

import java.io.Serializable;

public class UserRollbackDto implements Serializable {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
