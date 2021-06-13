package caprita.catalin.cityquestbackend.controllers.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCompanionDto {
    private String name;
    private String attributeKey;
    private String attributeValue;
}
