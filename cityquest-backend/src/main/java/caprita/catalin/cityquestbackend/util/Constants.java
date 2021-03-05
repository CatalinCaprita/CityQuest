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

}
