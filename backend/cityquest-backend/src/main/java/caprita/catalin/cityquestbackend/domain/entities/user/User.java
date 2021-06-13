package caprita.catalin.cityquestbackend.domain.entities.user;

import caprita.catalin.cityquestbackend.domain.entities.BaseEntity;
import caprita.catalin.cityquestbackend.domain.entities.UserLocationRating;
import caprita.catalin.cityquestbackend.domain.entities.UserSubtaskResponse;
import caprita.catalin.cityquestbackend.domain.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "USER", uniqueConstraints = {
        @UniqueConstraint(columnNames = "USERNAME"),
        @UniqueConstraint(columnNames = "EMAIL"),

})
@Getter
@Setter
public class User extends BaseEntity {

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name= "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "GENDER")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "JOIN_DATE")
    private LocalDate joinDate;

    @Column(name = "KNOWLEDGE", columnDefinition = "int default 0")
    private Integer knowledge = 0;
    @Column(name = "VITALITY",columnDefinition = "int default 0")
    private Integer vitality = 0;
    @Column(name = "SWIFTNESS",columnDefinition = "int default 0")
    private Integer swiftness = 0;
    @Column(name = "SOCIABILITY",columnDefinition ="int default 0")
    private Integer sociability = 0;

    @Transient
    private double[] scores =  new double[5];

    @Column(name = "OTE_SCORE",precision = 10,scale = 9)
    private BigDecimal oteScore;

    @Column(name = "CON_SCORE",precision = 10,scale = 9)
    private BigDecimal conScore;

    @Column(name = "EXT_SCORE",precision = 10,scale = 9)
    private BigDecimal extScore;

    @Column(name = "AGR_SCORE",precision = 10,scale = 9)
    private BigDecimal agrScore;

    @Column(name = "NEUR_SCORE",precision = 10,scale = 9)
    private BigDecimal neurScore;


    @OneToMany(fetch = FetchType.LAZY,
    orphanRemoval = true,
    mappedBy = "user")
    private Set<UserLocationRating> locationRatings = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY,
    orphanRemoval = true,
    mappedBy = "user")
    private Set<UserCompanion> companions = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST,
            CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public double[] getScores() {
        return scores;
    }

    public void setScores(double[] bfiScores) {
        this.setExtScore(BigDecimal.valueOf(bfiScores[0]));

        this.setAgrScore(BigDecimal.valueOf(bfiScores[1]));
        this.setConScore(BigDecimal.valueOf(bfiScores[2]));
        this.setNeurScore(BigDecimal.valueOf(bfiScores[3]));
        this.setOteScore(BigDecimal.valueOf(bfiScores[4]));

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return getUsername().equals(user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUsername());
    }

    public void addCompanion(UserCompanion companion){
        this.companions.add(companion);
        companion.setUser(this);
    }

    public void removeCompanion(UserCompanion companion){
        this.companions.remove(companion);
        companion.setUser(null);
    }
}
