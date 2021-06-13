package caprita.catalin.cityquest.ui.api.register;

import caprita.catalin.cityquest.ui.models.UserModel;
import io.reactivex.Flowable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RegisterApi {

    @POST("/api/users/register")
    Flowable<Response<UserModel>> registerNewUser(@Body RegisterDto registerDto);
    @POST("/api/users/register")
    Flowable<Response<Void>> rollbackRegistration(@Body UserRollbackDto registerDto);

    @PATCH("/api/users/register")
    Flowable<Response<UserModel>> completeUserRegistration(@Body UserRegisterDetailsDto dataDto);

    @GET("/api/users/register/predict")
    Flowable<Response<Void>> computeUserPredictions(@Query("userId") Long userId);

}
