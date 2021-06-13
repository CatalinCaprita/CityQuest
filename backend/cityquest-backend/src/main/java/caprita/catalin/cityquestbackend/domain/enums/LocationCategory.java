package caprita.catalin.cityquestbackend.domain.enums;

import java.util.Arrays;
import java.util.List;

public enum LocationCategory {
    NATURE_PARKS,
    MUSEUMS,
    LANDMARKS,
    RESTAURANTS,
    CLUBS;

    public static List<LocationCategory> listAll(){return Arrays.asList(LocationCategory.values());}
}
