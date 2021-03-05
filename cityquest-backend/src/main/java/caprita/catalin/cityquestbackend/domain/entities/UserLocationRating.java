package caprita.catalin.cityquestbackend.domain.entities;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlIDREF;

@Entity
@Table(name = "USER_LOCATION_RATING")
public class UserLocationRating {

    @EmbeddedId
    UserLocationKey id;

    @ManyToOne
    @MapsId("USER_ID")
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @MapsId("LOCATION_ID")
    @JoinColumn(name = "LOCATION_ID")
    private Location location;

    @Column(name = "RATING")
    private int rating;

    public UserLocationRating() {
    }

    public UserLocationRating(UserLocationKey id, int rating) {
        this.id = id;
        this.rating = rating;
    }

    public UserLocationKey getId() {
        return id;
    }

    public void setId(UserLocationKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
