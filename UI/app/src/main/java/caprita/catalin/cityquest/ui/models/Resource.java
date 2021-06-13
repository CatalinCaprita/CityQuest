package caprita.catalin.cityquest.ui.models;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    @Nullable
    public final Integer code;


    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable Integer code) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public static <T> Resource<T> success (@Nullable T data){
        return new Resource<>(Status.SUCCESS, data, null, null);
    };

    public static <T> Resource<T> error(@NonNull String msg, @Nullable T data){
        return new Resource<>(Status.ERROR, data, msg, null);
    };

    public static <T> Resource<T> error(@NonNull String msg, @Nullable T data, @Nullable Integer code){
        return new Resource<>(Status.ERROR, data, msg, code);
    };

    public static  <T> Resource<T> loading(@Nullable T data){
        return new Resource<>(Status.LOADING, data, null, null);
    }
    public enum Status { SUCCESS, ERROR, LOADING}

}