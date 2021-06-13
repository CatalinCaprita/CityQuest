package caprita.catalin.cityquestbackend.util;

import caprita.catalin.cityquestbackend.domain.entities.Location;
import caprita.catalin.cityquestbackend.domain.entities.user.User;
import caprita.catalin.cityquestbackend.domain.enums.LocationCategory;
import caprita.catalin.cityquestbackend.services.location.LocationService;
import caprita.catalin.cityquestbackend.services.ratings.RatingService;
import caprita.catalin.cityquestbackend.services.user.UserService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static caprita.catalin.cityquestbackend.util.Constants.Form.*;

import java.io.FileReader;
import java.util.*;

@Component
public class CsvUtils {
    @Value("${CSV_FILE_PATH}")
    private String filePath;

    private final CsvMapper mapper;
    private final CsvSchema schema;
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtils.class);
    private final Map<String, Long> locationQuestionId = new HashMap<>();
    private final LocationService locationService;
    private final UserService userService;
    private final RatingService ratingService;
    private final ModelMapper modelMapper;

    @Autowired
    public CsvUtils(LocationService locationService,
                    ModelMapper modelMapper,
                    UserService userService,
                    RatingService ratingService) {
        this.mapper = new CsvMapper();
        this.schema = CsvSchema.emptySchema().withNullValue("").withHeader();
        this.locationService = locationService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.ratingService  =ratingService;

    }

//    @PostConstruct
    public void parseFormsCsv(){
        ObjectReader objectReader = mapper.readerFor(Map.class).with(schema);
        List<UserAnswerDto> dtos = new ArrayList<>();
        String[]locations = new String[0] ;
        try(FileReader reader = new FileReader(filePath)){
            boolean first = true;
            MappingIterator<Map<String,Object>> it = objectReader.readValues(reader);

            while( it.hasNext()){
                Map<String,Object> dto = it.next();
                if (first){
                    locations = dto.keySet().toArray(locations);
                    insertLocations(locations);
                    return;
                }
                dtos.add(mapToEntity(dto));
            }

        }catch (Exception e){
            LOGGER.error("Failed to parse csv: {}", e.getMessage());
        }
        insertLocations(locations);
        insertUsers(dtos);
    }

    private UserAnswerDto mapToEntity(Map<String,Object> parsed){
        UserAnswerDto dto = new UserAnswerDto();
        int category = 0;
        int categoryIndex = 0;
        String[] keyNames = parsed.keySet().toArray(new String[0]);
        for(Map.Entry<String,Object> record : parsed.entrySet()){
            if (record.getValue().equals(""))
                record.setValue("0");
            if(SIZES[category] == categoryIndex) {
                category++;
                categoryIndex = 0;
            }
            switch (category){
                case TS: {dto.timestamp = (String)record.getValue();break;}
                case BFI:{dto.personalityTest[categoryIndex] = Integer.parseInt((String)record.getValue());break;}
                case RESTAURANTS:{
                    if (categoryIndex == SIZES[category] - 1)
                        dto.restaurantsRecs = (String)record.getValue();
                    else
                        dto.locationRatings.put(record.getKey(),Integer.parseInt((String)record.getValue()));
                    break;
                }
                case CLUBS:{
                    if (categoryIndex == SIZES[category] - 1) {
                        dto.clubsRecs = (String) record.getValue();
                        LOGGER.info("Detected new Club Recommendation: {}", dto.clubsRecs);
                    }
                    else
                        dto.locationRatings.put(record.getKey(),Integer.parseInt((String)record.getValue()));
                    break;
                }
                default:{ dto.getLocationRatings().put(record.getKey(), Integer.parseInt((String)record.getValue())) ;break; }
            }
            categoryIndex++;
            if(category > BFI){
                if((category == RESTAURANTS || category == CLUBS) && categoryIndex == SIZES[category] - 1){
                    continue;
                }
            }
        }
        return dto;
    }

    private void insertLocations(String[] locations){
        LOGGER.info("BootStrapping Locations from a DTO. Containing TS and BFI. Ignoring those.");
        int category = NATURE;
        int categoryIndex = 0;
        for(int i = INDEXES[category]; i < locations.length; i++){

            if(SIZES[category] == categoryIndex) {
                category++;
                categoryIndex = 0;
            }
            LocationCategory locationCategory = LocationCategory.CLUBS;
            switch (category){
                case NATURE:{locationCategory = LocationCategory.NATURE_PARKS;break;}
                case MUSEUMS:{locationCategory = LocationCategory.MUSEUMS;break;}
                case LANDMARKS:{locationCategory = LocationCategory.LANDMARKS;break;}
                case RESTAURANTS:{
                    if (categoryIndex < SIZES[category] - 1)
                        locationCategory = LocationCategory.RESTAURANTS;
                    break;
                }
                case CLUBS:{
                    if (categoryIndex < SIZES[category] - 1)
                        locationCategory = LocationCategory.CLUBS;
                    break;
                }
            }
            if((category == RESTAURANTS || category == CLUBS) && categoryIndex == SIZES[category] - 1){
                categoryIndex ++;
                LOGGER.debug("Skipping over last index of category {}",category);
                continue;
            }
            categoryIndex++;
            Location newLocation = new Location();
            newLocation.setCategory(locationCategory);
            newLocation.setName(locations[i].substring(locations[i].indexOf("[") + 1,locations[i].indexOf("]")).trim());
            newLocation = locationService.save(newLocation);
            locationQuestionId.put(locations[i], newLocation.getId());

            LOGGER.debug("Putting {} inside key {} with id: {} ",newLocation.getName(),
                    locations[i], newLocation.getId());
        }
    }

    private void insertUsers(List<UserAnswerDto> dtos){
        dtos.forEach(dto -> {
            //Compute BFI SCORE from answers
            User newUser = modelMapper.map(dto,User.class);
            //Persist the user
            final User finalUser = userService.createFromFile(newUser);
            //Add Ratings
            dto.getLocationRatings().entrySet().forEach( entrySet ->{
                if(entrySet.getValue() > 0 )
                    ratingService.addUserLocationRating(finalUser.getId(),
                            locationQuestionId.get(entrySet.getKey()),
                            entrySet.getValue());
            });
            //Select Restaurants recommendations and persist them as locations
            if(dto.getRestaurantsRecs() != null &&!dto.getRestaurantsRecs().isEmpty()) {
                Arrays.stream(dto.getRestaurantsRecs().split(",")).forEach(rec -> {
                    if(!rec.equals("0"))
                        addUserRecommendation(LocationCategory.RESTAURANTS,rec.trim(), finalUser.getId());
                });
            }
            if(dto.getClubsRecs() != null && !dto.getClubsRecs().isEmpty()) {
                Arrays.stream(dto.getClubsRecs().split(",")).forEach(rec -> {
                    if(!rec.equals("0"))
                        addUserRecommendation(LocationCategory.CLUBS, rec.trim(), finalUser.getId());
                });
            }

        });

    }
    private void addUserRecommendation(LocationCategory category,String recName, Long userId){
        /*
         * First off look up the location by name.
         * */
        Location location = locationService.findByName(recName.trim());

        if(location == null){
            Location recommendedLocation = new Location();
            recommendedLocation.setName(recName.trim());
            recommendedLocation.setCategory(category);
            Long locationId = locationService.save(recommendedLocation).getId();
            ratingService.addUserLocationRating(userId,
                    locationId,
                    3);
            return;
        }
        ratingService.addUserLocationRating(userId,
                location.getId(),
                3);
    }
}
