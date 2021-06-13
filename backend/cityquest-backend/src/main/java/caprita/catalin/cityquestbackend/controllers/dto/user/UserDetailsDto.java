package caprita.catalin.cityquestbackend.controllers.dto.user;

import caprita.catalin.cityquestbackend.domain.enums.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsDto implements Serializable {
   private Long id;

   private String username;

   private String email;

   private String firstName;

   private String lastName;

   private Gender gender;

   private BigDecimal oteScore;

   private BigDecimal conScore;

   private BigDecimal extScore;

   private BigDecimal agrScore;

   private BigDecimal neurScore;

   private boolean isEnabled;

   private String joinDate;

   private List<UserCompanionDto> companions;
   private Integer knowledge;

   private Integer vitality;

   private Integer swiftness;

   private Integer sociability;

}
