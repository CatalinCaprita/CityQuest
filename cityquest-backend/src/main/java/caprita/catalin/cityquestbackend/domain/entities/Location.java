package caprita.catalin.cityquestbackend.domain.entities;

import caprita.catalin.cityquestbackend.domain.enums.LocationCategory;
import caprita.catalin.cityquestbackend.util.Constants;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static caprita.catalin.cityquestbackend.util.Constants.SEQ_PREFIX;
import static caprita.catalin.cityquestbackend.util.Constants.SEQ_SUFFIX;

@Entity
@Table(name = "LOCATION", uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
//@SequenceGenerator(name = Constants.DEFAULT_SEQ_GEN, sequenceName = SEQ_PREFIX + "LOCATION" + SEQ_SUFFIX,
//        allocationSize = 1,
//        initialValue = 1)
public class Location extends BaseEntity{

    @Column(name = "NAME")
    private String name;

    @Column(name = "CATEGORY")
    @Enumerated(EnumType.STRING)
    private LocationCategory category;

    @OneToMany(mappedBy = "location",
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    private Set<UserLocationRating> userRatings = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationCategory getCategory() {
        return category;
    }

    public void setCategory(LocationCategory category) {
        this.category = category;
    }

    public Set<UserLocationRating> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(Set<UserLocationRating> userRatings) {
        this.userRatings = userRatings;
    }
}
