package caprita.catalin.cityquestbackend.domain.entities.user;

import caprita.catalin.cityquestbackend.domain.entities.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "USER_COMPANION")
public class UserCompanion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name= "NAME", nullable = false)
    private String name;
//
//    @Column(name = "ATTR_KEY", nullable = true)
//    private String attributeKey;

    @Column(name = "NICKNAME", nullable = true)
    private String nickname;

}
