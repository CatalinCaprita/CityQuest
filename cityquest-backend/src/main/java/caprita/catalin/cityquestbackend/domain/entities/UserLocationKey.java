package caprita.catalin.cityquestbackend.domain.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserLocationKey implements Serializable {
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public UserLocationKey() {
    }

    public UserLocationKey(Long userId, Long locationId) {
        this.userId = userId;
        this.locationId = locationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserLocationKey)) return false;
        UserLocationKey that = (UserLocationKey) o;
        return getUserId().equals(that.getUserId()) && getLocationId().equals(that.getLocationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getLocationId());
    }
}
