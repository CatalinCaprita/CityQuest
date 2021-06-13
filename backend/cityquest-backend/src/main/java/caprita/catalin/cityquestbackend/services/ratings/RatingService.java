package caprita.catalin.cityquestbackend.services.ratings;

import caprita.catalin.cityquestbackend.domain.entities.UserLocationRating;
import caprita.catalin.cityquestbackend.services.prediction.PredictionServiceImpl;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.NoSuchElementException;

public interface RatingService {
    void addUserLocationRating(Long userId, Long locationId, double rating) throws PersistenceException;
    void addUserLocationRating(Long userId, Long locationId, double rating, boolean isReal) throws PersistenceException;
    void addUserLocationPrediction(Long userId, PredictionServiceImpl.LocationRating rating) throws PersistenceException;
    void addAllForUser(Long userId, List<PredictionServiceImpl.LocationRating> ratings) throws PersistenceException;
    void markUserLocationAsVisited(Long userId, Long locationId) throws NoSuchElementException, InternalError;
    UserLocationRating findByIds(Long userId, Long locationId);
}
