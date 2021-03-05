package caprita.catalin.cityquestbackend.services.ratings;

import caprita.catalin.cityquestbackend.domain.entities.UserLocationRating;

public interface RatingService {
    void addUserLocationRating(Long userId, Long locationId, int rating);
    UserLocationRating findByIds(Long userId, Long locationId);
}
