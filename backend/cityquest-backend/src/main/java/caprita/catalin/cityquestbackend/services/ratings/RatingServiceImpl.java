package caprita.catalin.cityquestbackend.services.ratings;

import caprita.catalin.cityquestbackend.domain.entities.UserLocationKey;
import caprita.catalin.cityquestbackend.domain.entities.UserLocationRating;
import caprita.catalin.cityquestbackend.repositories.user.UserLocationRatingRepository;
import caprita.catalin.cityquestbackend.services.prediction.PredictionServiceImpl;
import caprita.catalin.cityquestbackend.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RatingServiceImpl implements RatingService{

    private final UserLocationRatingRepository userLocationRatingRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RatingService.class);

    @Autowired
    public RatingServiceImpl(UserLocationRatingRepository userLocationRatingRepository) {
        this.userLocationRatingRepository = userLocationRatingRepository;
    }

    @Override
    public void addUserLocationRating(Long userId, Long locationId, double rating, boolean isReal) throws PersistenceException {
        UserLocationKey key = new UserLocationKey(userId,locationId);
        UserLocationRating entity = userLocationRatingRepository.findById(key).orElse(null);
        if(entity == null){
            entity = new UserLocationRating();
            entity.setId(key);
        }
        entity.setRating(BigDecimal.valueOf(rating));
        entity.setReal(isReal);
        if(!isReal) {
            LOGGER.info("Added NEW User-Location PREDICTED rating for userId: {} ,locationId: {}, rating: {}",
                    entity.getId().getUserId(),entity.getId().getLocationId(),entity.getRating());
            entity.setPrediction(true);
        }
        try {
            entity = userLocationRatingRepository.save(entity);
        }catch (Exception e){
            LOGGER.error("Rating persistance erorr: {}", e.getMessage());
            throw new PersistenceException(e.getMessage());
        }
    }

    @Override
    public void addUserLocationPrediction(Long userId, PredictionServiceImpl.LocationRating rating) throws PersistenceException {
        UserLocationKey key = new UserLocationKey(userId, rating.locationId);
        UserLocationRating entity = userLocationRatingRepository.findById(key).orElse(null);
        if(entity == null){
            entity = new UserLocationRating();
            entity.setId(key);
        }
        entity.setRating(BigDecimal.valueOf(rating.rating));
        entity.setReal(false);
        entity.setPrediction(true);
        entity.setLocationCategory(rating.category);
        entity.setWasVisited(false);
        LOGGER.info("Added NEW User-Location PREDICTED rating for userId: {} ,locationId: {}, rating: {}",
                    entity.getId().getUserId(),entity.getId().getLocationId(),entity.getRating());
        try {
            entity = userLocationRatingRepository.save(entity);
        }catch (Exception e){
            LOGGER.error("Rating persistance erorr: {}", e.getMessage());
            throw new PersistenceException(e.getMessage());
        }
    }

    @Override
    public void addAllForUser(Long userId, List<PredictionServiceImpl.LocationRating> ratings) throws PersistenceException {

    }

    @Override
    public void markUserLocationAsVisited(Long userId, Long locationId) throws NoSuchElementException, InternalError {
        try {
            UserLocationKey key = new UserLocationKey(userId, locationId);
            UserLocationRating entity = userLocationRatingRepository.findById(key).orElseThrow();
            entity.setWasVisited(true);
            userLocationRatingRepository.save(entity);
        }catch (NoSuchElementException ex){
            throw ex;
        }catch (Exception e){
            throw new InternalError(e.getMessage());
        }
    }

    @Override
    public void addUserLocationRating(Long userId, Long locationId, double rating) throws PersistenceException {
        addUserLocationRating(userId, locationId, rating, true);
    }


    @Override
    public UserLocationRating findByIds(Long userId, Long locationId) {
        UserLocationKey key = new UserLocationKey(userId,locationId);
        return userLocationRatingRepository.findById(key).orElse(null);
    }
}
