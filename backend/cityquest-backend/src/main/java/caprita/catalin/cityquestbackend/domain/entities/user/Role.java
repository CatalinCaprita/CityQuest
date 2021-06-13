package caprita.catalin.cityquestbackend.domain.entities.user;

import caprita.catalin.cityquestbackend.domain.entities.BaseEntity;
import caprita.catalin.cityquestbackend.domain.enums.RoleCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ROLE",
        uniqueConstraints = {@UniqueConstraint(columnNames = "CODE")})
@Getter
@Setter
public class Role extends BaseEntity {
    @Column(name = "CODE", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleCode code;
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

}
