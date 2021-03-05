package caprita.catalin.cityquestbackend.services.ratings;

import caprita.catalin.cityquestbackend.domain.entities.User;
import caprita.catalin.cityquestbackend.domain.entities.UserLocationKey;
import caprita.catalin.cityquestbackend.domain.entities.UserLocationRating;
import caprita.catalin.cityquestbackend.repositories.UserLocationRatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImpl implements RatingService{

    private final UserLocationRatingRepository userLocationRatingRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RatingService.class);

    @Autowired
    public RatingServiceImpl(UserLocationRatingRepository userLocationRatingRepository) {
        this.userLocationRatingRepository = userLocationRatingRepository;
    }

    @Override
    public void addUserLocationRating(Long userId, Long locationId, int rating) {
        UserLocationKey key = new UserLocationKey(userId,locationId);
        UserLocationRating entity = userLocationRatingRepository.findById(key).orElse(null);
        if(entity == null){
            entity = new UserLocationRating();
            entity.setId(key);
            entity.setRating(rating);
            entity = userLocationRatingRepository.save(entity);
            LOGGER.debug("Added new User-Location rating for userId{} ,locationId{}, rating {}",
                    entity.getId().getUserId(),entity.getId().getLocationId(),entity.getRating());
        }else{
            entity.setRating(rating);
            entity = userLocationRatingRepository.save(entity);
            LOGGER.debug("Updated User-Location rating for userId{} ,locationId{}, rating {}",
                    entity.getId().getUserId(),entity.getId().getLocationId(),entity.getRating());
        }
    }

    @Override
    public UserLocationRating findByIds(Long userId, Long locationId) {
        UserLocationKey key = new UserLocationKey(userId,locationId);
        return userLocationRatingRepository.findById(key).orElse(null);
    }
}
