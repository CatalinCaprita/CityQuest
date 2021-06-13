package caprita.catalin.cityquestbackend.domain.entities;

import caprita.catalin.cityquestbackend.domain.entities.quest.Quest;
import caprita.catalin.cityquestbackend.domain.enums.LocationCategory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "LOCATION", uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
//@SequenceGenerator(name = Constants.DEFAULT_SEQ_GEN, sequenceName = SEQ_PREFIX + "LOCATION" + SEQ_SUFFIX,
//        allocationSize = 1,
//        initialValue = 1)
@Getter
@Setter
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

    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "location",
            orphanRemoval = true)
    private Quest quest;

}
