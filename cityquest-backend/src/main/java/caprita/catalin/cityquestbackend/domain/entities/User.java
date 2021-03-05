package caprita.catalin.cityquestbackend.domain.entities;

import caprita.catalin.cityquestbackend.domain.entities.BaseEntity;
import caprita.catalin.cityquestbackend.domain.entities.Location;
import caprita.catalin.cityquestbackend.util.Constants;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static caprita.catalin.cityquestbackend.util.Constants.SEQ_PREFIX;
import static caprita.catalin.cityquestbackend.util.Constants.SEQ_SUFFIX;

@Entity
@Table(name = "USER", uniqueConstraints = @UniqueConstraint(columnNames = "USERNAME"))
//@SequenceGenerator(name = Constants.DEFAULT_SEQ_GEN, sequenceName = SEQ_PREFIX + "USER" + SEQ_SUFFIX,
//allocationSize = 1,
//initialValue = 1)
public class User extends BaseEntity {

    @Column(name = "USERNAME")
    private String username;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getOteScore() {
        return oteScore;
    }

    public void setOteScore(BigDecimal oteScore) {
        this.oteScore = oteScore;
    }

    public BigDecimal getConScore() {
        return conScore;
    }

    public void setConScore(BigDecimal conScore) {
        this.conScore = conScore;
    }

    public BigDecimal getExtScore() {
        return extScore;
    }

    public void setExtScore(BigDecimal extScore) {
        this.extScore = extScore;
    }

    public BigDecimal getAgrScore() {
        return agrScore;
    }

    public void setAgrScore(BigDecimal agrScore) {
        this.agrScore = agrScore;
    }

    public BigDecimal getNeurScore() {
        return neurScore;
    }

    public void setNeurScore(BigDecimal neurScore) {
        this.neurScore = neurScore;
    }

    public Set<UserLocationRating> getLocationRatings() {
        return locationRatings;
    }

    public void setLocationRatings(Set<UserLocationRating> locationRatings) {
        this.locationRatings = locationRatings;
    }

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
}
