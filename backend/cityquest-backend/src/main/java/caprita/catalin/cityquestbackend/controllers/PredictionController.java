package caprita.catalin.cityquestbackend.controllers;

import caprita.catalin.cityquestbackend.controllers.dto.location.LocationListingDTO;
import caprita.catalin.cityquestbackend.services.prediction.PredictionException;
import caprita.catalin.cityquestbackend.services.prediction.PredictionService;
import caprita.catalin.cityquestbackend.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(PredictionController.PREDICTION_API)
public class PredictionController {
    public static final String PREDICTION_API = Constants.Api.BASE_API_URL + "/users/register/predict";
    private  final PredictionService predictionService;

    @Autowired
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public ResponseEntity<?> computePredictions(@RequestParam Long userId){
        try{
            predictionService.computePredictions(userId);
            return ResponseEntity.ok().build();
        }catch (PredictionException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }catch( UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
