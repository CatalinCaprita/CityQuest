package caprita.catalin.cityquestbackend.controllers.dto.prediction;

import caprita.catalin.cityquestbackend.domain.enums.LocationCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PredictionRequestDTO implements Serializable {
    public static final String SIGNATURE_NAME = "signature_name";
    public static final String INPUTS = "inputs";


    private final  String signature_name = "";
    private final  PredictionInputs inputs = new PredictionInputs();

    @Getter
    @Setter
    public static final class PredictionInputs implements Serializable{
        float [] ote_score;
        float [] con_score;
        float [] ext_score;
        float [] agr_score;
        float [] neur_score;
        int [] user_id;
        int [] location_id;
        String [] location_category;
    }
}
