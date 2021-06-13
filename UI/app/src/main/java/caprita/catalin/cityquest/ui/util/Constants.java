package caprita.catalin.cityquest.ui.util;

public class Constants {

    public static final class API{
        public static final String BASE_URL = "https://cityquest.pagekite.me/";
        public static final String USERS_URL = BASE_URL + "users/";
    }
    public static final class Error{
        public static final String COULD_NOT_LOCATE_USER = "Could not find user details";
        public static final String USER_CREDS_TAKEN = "We know that username or email already.";
        public static final String SOMETHING_WENT_WRONG = "Something went wrong.Try again a little later.";
        public static final String PREDICTION_ERROR = "It seems we were able to register you, but could not find " +
                "quests worthy of you";
        public static final String EMPTY_LIST = "Could not fetch any new results";
        public static final int CODE_NULL_DATA = -1;
    }
    public static final class Quest{
        public static final int SUBTASK_QUIZ_ANSWER_COUNT = 4;
        public static final float MAP_BOUND_OFFSET = 5e-3f;
    }
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
}
