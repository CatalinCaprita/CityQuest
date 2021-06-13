package caprita.catalin.cityquestbackend.services.prediction;

import caprita.catalin.cityquestbackend.controllers.dto.prediction.PredictionRequestDTO;
import caprita.catalin.cityquestbackend.controllers.dto.prediction.PredictionResultDTO;
import caprita.catalin.cityquestbackend.domain.entities.UserQuestLog;
import caprita.catalin.cityquestbackend.domain.entities.UserQuestLogKey;
import caprita.catalin.cityquestbackend.domain.entities.user.User;
import caprita.catalin.cityquestbackend.domain.enums.LocationCategory;
import caprita.catalin.cityquestbackend.domain.enums.QuestStatus;
import caprita.catalin.cityquestbackend.repositories.user_quest.UserQuestLogRepository;
import caprita.catalin.cityquestbackend.services.location.LocationService;
import caprita.catalin.cityquestbackend.services.ratings.RatingService;
import caprita.catalin.cityquestbackend.services.user.UserService;
import caprita.catalin.cityquestbackend.util.Constants;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.*;

@Service
public class PredictionServiceImpl implements PredictionService{
    private final UserService userService;
    private final LocationService locationService;
    private final RatingService ratingService;
    private final UserQuestLogRepository uqlRepository;
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;
    private final String PREDICT_BASE_URL = "http://localhost:8501";
    private final String PREDICT_URI = "/v1/models/ranker:predict";
    private static final int TOP_NUMBER = 5;
    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionService.class);

    @Autowired
    public PredictionServiceImpl(UserService userService,
                                 LocationService locationService,
                                 RatingService ratingService,
                                 UserQuestLogRepository uqlRepository,
                                 RestTemplate restTemplate,
                                 ModelMapper modelMapper) {
        this.userService = userService;
        this.locationService = locationService;
        this.restTemplate = restTemplate;
        this.modelMapper = modelMapper;
        this.ratingService = ratingService;
        this.uqlRepository = uqlRepository;
    }

    @Override
    public void computePredictions(Long newUserId) throws PredictionException, UsernameNotFoundException {
        User user = userService.findById(newUserId);
        if(user == null){
            throw new UsernameNotFoundException(String.format(Constants.Error.COULD_NOT_LOCATE_USER, newUserId));
        }
        PredictionRequestDTO requestDTO = new PredictionRequestDTO();
        PredictionRequestDTO.PredictionInputs inputs = requestDTO.getInputs();

        int[] locationIds = locationService.getAllIds()
                .stream()
                .mapToInt(Long::intValue)
                .toArray();

        String [] categories = locationService.getAllCategories()
                .toArray(new String[0]);

        int len = locationIds.length;
        float [] os = new float[len];
        float [] cs = new float[len];
        float [] es = new float[len];
        float [] as = new float[len];
        float [] ns = new float[len];

        /*Fill up the con scores*/
        Arrays.fill(os, user.getOteScore().longValue());
        Arrays.fill(cs, user.getConScore().longValue());
        Arrays.fill(es, user.getExtScore().longValue());
        Arrays.fill(as, user.getAgrScore().longValue());
        Arrays.fill(ns, user.getNeurScore().longValue());

        /*Fill up the user_id part*/
        int [] uid = new int[len];
        Arrays.fill(uid, user.getId().intValue());
        inputs.setAgr_score(as);
        inputs.setCon_score(cs);
        inputs.setExt_score(es);
        inputs.setOte_score(os);
        inputs.setNeur_score(ns);
        inputs.setUser_id(uid);

        inputs.setLocation_id(locationIds);
        inputs.setLocation_category(categories);
        List<Long> topNLocationIds = new ArrayList<>();

            try {
                HttpEntity<PredictionRequestDTO> request = new HttpEntity<>(requestDTO);

                /*Post for the TFServing service*/
                ResponseEntity<PredictionResultDTO> response = restTemplate.exchange(PREDICT_BASE_URL + PREDICT_URI,
                        HttpMethod.POST, request, PredictionResultDTO.class);
                /*Since I posted for the list of increasing location_id's
                * I expect that output for k is prediction for location_id[k]*/
                Map<Double, LocationRating> outIndexToLocationId = new HashMap<>();
                /*outputs[i] = predicted score for locationId[i]*/
                double[] outputs = Arrays.stream(response.getBody().getOutputs())
                        .map(array -> array[0])
                        .mapToDouble(Float::floatValue)
                        .toArray();
                for (int k = 0; k < outputs.length; k++) {
                    outIndexToLocationId.put(outputs[k],
                            new LocationRating((long) inputs.getLocation_id()[k],
                                    inputs.getLocation_category()[k],
                                    outputs[k]));
//                    Add a rating that is predicted, NOT REAL
                }


//                So, for each location category, we compute the top 5 recommendations
                Map<LocationCategory, List<LocationRating>> topFiveByCategory = new HashMap<>();
                Arrays.stream(outputs)
                        .boxed()
                        .sorted(Comparator.reverseOrder())
                        .map(outIndexToLocationId::get)
                        .forEach(locationRating -> {
                            if(!topFiveByCategory.containsKey(locationRating.category)){
                                topFiveByCategory.put(locationRating.category, new ArrayList<>());
                            }
                            if(topFiveByCategory.get(locationRating.category).size() < TOP_NUMBER )
                                topFiveByCategory.get(locationRating.category).add(locationRating);
                        });
                topFiveByCategory.entrySet().forEach(locationCategoryListEntry -> {
                    locationCategoryListEntry.getValue().forEach(locationRating -> {
                        try {
                            ratingService.addUserLocationPrediction(newUserId, locationRating);
                            addQuestAsRemaining(locationRating.locationId, newUserId);
                        }catch (PersistenceException e){
                            LOGGER.error("Could not perform addition.");
                        }
                    });
                });
                LOGGER.debug("Ended sorting.");
            } catch (Exception e) {
                e.printStackTrace();
                throw new PredictionException(e.getMessage());
            }

    }

    public final class LocationRating{
        public final Long locationId;
        public final LocationCategory category;
        public final double rating;
        public LocationRating(Long locationId, String category, double rating) {
            this.locationId = locationId;
            this.category = LocationCategory.valueOf(category);
            this.rating = rating;
        }
    }

    private void addQuestAsRemaining(Long locationId, Long userId){
        try {
            UserQuestLogKey key = new UserQuestLogKey(userId, locationId);
            UserQuestLog log = uqlRepository.findById(key).orElse(null);
            if (log == null) {
                log = new UserQuestLog();
                log.setId(key);
            }
            log.setProgress(QuestStatus.REMAINING);
            uqlRepository.save(log);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }
}
