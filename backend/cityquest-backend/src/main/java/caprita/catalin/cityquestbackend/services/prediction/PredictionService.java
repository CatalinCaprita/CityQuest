package caprita.catalin.cityquestbackend.services.prediction;

import caprita.catalin.cityquestbackend.controllers.dto.location.LocationListingDTO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface PredictionService {
    void computePredictions(Long newUserId) throws PredictionException, UsernameNotFoundException;

}
