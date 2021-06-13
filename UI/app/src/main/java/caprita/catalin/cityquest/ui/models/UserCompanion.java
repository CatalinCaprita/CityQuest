package caprita.catalin.cityquest.ui.models;


public class UserCompanion {
    private Long id;
    private String name;
    private String nickname;
    private int imageResource;

    public UserCompanion(Long id, String name, String nickname, int imageResource) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.imageResource = imageResource;
    }

    public UserCompanion() {
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
