package caprita.catalin.cityquest.ui.api;

import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.util.Constants;
import io.reactivex.annotations.NonNull;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class ResponseResourceConverter{
    private static final String TAG = "ResponseResourceConvert";

    /**
     * Generic static method that will allow for conversion from a Response<T>
     * received from an Api Call
     * @return : Resource.success(T) if response is successful, <br>
     *           Resource.error with a non-null 'message' and a non-null 'code'
     * @throws  IOException
     * */
    public static <T> Resource<T> buildResourceFromResponse(@NonNull Response<T> response) throws IOException {
        if (response.isSuccessful()) {
            Log.d(TAG, "buildResourceFromResponse: Successful return!");
            return Resource.success(response.body());
        }
        String errm;
        switch (response.code()) {
            case 500:
                errm = Constants.Error.SOMETHING_WENT_WRONG;
                break;
            case 404:
                errm = Constants.Error.COULD_NOT_LOCATE_USER;
                break;
            default:
                errm = response.errorBody() != null ? response.errorBody().string() : Constants.Error.SOMETHING_WENT_WRONG;
                break;
        }
        return Resource.error(errm, (T) null, response.code());
    }
    /**
     * Generic static method that will allow for conversion from a Throwable error discovered
     * during a Retrofit Api call.
     * @return  Resource.error with a @value 'message', non-null 'code', and a null body
     * */
    @NonNull
    public static <T> Resource<T> buildResourceFromError(@NonNull Throwable err) {
        err.printStackTrace();
        Log.e(TAG, "Error Encountered");
        if (err instanceof HttpException) {
            HttpException err2 = (HttpException) err;
            return Resource.error(err2.message(), (T) null, err2.code());
        }
        return Resource.error(err.getMessage(), (T) null, 500);
    }

    /**
     * Generic method that will return an OkHttp3 Response wrapper class from an error occuring
     * during an Http call.
     * @return  Response.error with code of the http response, it the error is an HttpException
     * <br>
     *     Response.error with 500 code if the error was not an HttpException
     * */
    @NonNull
    public static <T> Response<T> buildResponseFromError(@NonNull Throwable err){
        if(err instanceof HttpException){
            HttpException err2 = (HttpException) err;
            if(err2.response().errorBody() != null)
                return Response.error(err2.code(), err2.response().errorBody());
            return Response.error(err2.code(), ResponseBody.create(MediaType.parse("text/plain"), err.getMessage()));
        }
        return Response.error(500, ResponseBody.create(MediaType.parse("text/plain"), err.getMessage()));
    }
}
