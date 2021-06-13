package caprita.catalin.cityquestbackend.controllers.dto.prediction;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PredictionResultDTO implements Serializable {
    private float[][] outputs;
    public PredictionResultDTO(){}
}
