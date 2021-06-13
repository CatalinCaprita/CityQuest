package caprita.catalin.cityquestbackend.domain.entities;

import caprita.catalin.cityquestbackend.domain.entities.user.User;
import caprita.catalin.cityquestbackend.domain.enums.LocationCategory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "USER_LOCATION_RATING")
@Getter
@Setter
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

    @Column(name = "RATING",precision = 10, scale = 9)
    private BigDecimal rating;

    @Column(name = "LOCATION_CATEGORY",nullable = true)
    @Enumerated(EnumType.STRING)
    private LocationCategory locationCategory;

    @Column(name = "IS_REAL", nullable = true, updatable = true)
    private boolean isReal;
    @Column(name = "IS_PREDICTION", nullable = true, updatable = true)
    private boolean isPrediction;
    @Column(name = "VISITED", nullable = true, updatable = true)
    private boolean wasVisited;

    public UserLocationRating() {
    }

    public UserLocationRating(UserLocationKey id) {
        this.id = id;
    }

}
