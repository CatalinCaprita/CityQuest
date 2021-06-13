package caprita.catalin.cityquestbackend.util;

public class Constants {
    public static final String DEFAULT_SEQ_GEN = "ID_SEQUENCE_GENERATOR";
    public static final String SEQ_PREFIX = "SEQ_";
    public static final String SEQ_SUFFIX = "_ID";

    public static final class Form{
        public static final int TS = 0;
        public static final int BFI = 1;
        public static final int NATURE = 2;
        public static final int MUSEUMS = 3;
        public static final int LANDMARKS = 4;
        public static final int RESTAURANTS = 5;
        public static final int CLUBS = 6;
        public static final int[] INDEXES = new int[7];
        public static final int[] SIZES = new int[]{1,44,10,8,10,24,11};
        static {
            INDEXES[TS] = 0;
            for(int i = 1; i< INDEXES.length; i++){
                INDEXES[i] = INDEXES[i-1] + SIZES[i-1];
            }
        }
        public Form(){
        }
    }
    public static final class Api{
        public static final String BASE_API_URL = "/api";
        public static final String LOGIN_URL = BASE_API_URL + "/users/login";
    }

    public static final class Error{
        public static final String COULD_NOT_LOCATE_USER = "Could not find user %s";
        public static final String COULD_NOT_LOCATE_LOCATION = "Could not find location %s";
        public static final String COULD_NOT_LOCATE_QUEST = "Could not find quest associated with location %s";
        public static final String USER_CREDS_TAKEN = "We know that username or email already.";
        public static final String QUEST_EXISTS = "There can only be one quest for this location";
        public static final String SOMETHING_WENT_WRONG = "Something went wrong.Try again a little later.";

    }


}
