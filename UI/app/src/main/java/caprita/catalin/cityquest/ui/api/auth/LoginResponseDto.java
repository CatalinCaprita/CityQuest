package caprita.catalin.cityquest.ui.api.auth;

import java.io.Serializable;

public class LoginResponseDto implements Serializable {

    private String username;
    private String token;
    private Long id;

    public LoginResponseDto(String username, String token, Long id) {
        this.username = username;
        this.token = token;
        this.id = id;
    }

    public LoginResponseDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
