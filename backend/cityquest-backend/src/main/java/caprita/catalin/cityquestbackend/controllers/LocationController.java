package caprita.catalin.cityquestbackend.controllers;

import caprita.catalin.cityquestbackend.controllers.dto.location.LocationWithQuestDto;
import caprita.catalin.cityquestbackend.services.quest.QuestService;
import caprita.catalin.cityquestbackend.services.location.LocationService;
import caprita.catalin.cityquestbackend.util.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(LocationController.LOCATIONS_URL)
public class LocationController {
    public static final String LOCATIONS_URL = Constants.Api.BASE_API_URL + "/locations";
    private final QuestService questService;
    private final LocationService locationService;
    private final ModelMapper modelMapper;

    @Autowired
    public LocationController(QuestService questService,
                              LocationService locationService,
                              ModelMapper modelMapper) {
        this.questService = questService;
        this.locationService = locationService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationWithQuestById(@PathVariable("id") Long id){
        try {
                LocationWithQuestDto dto = modelMapper.map(
                        locationService.findWithQuestBriefById(id), LocationWithQuestDto.class);
                return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    String.format(Constants.Error.COULD_NOT_LOCATE_LOCATION,
                    id));
        } catch (InternalError e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
