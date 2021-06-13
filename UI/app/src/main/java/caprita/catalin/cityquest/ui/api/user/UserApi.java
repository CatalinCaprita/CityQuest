package caprita.catalin.cityquest.ui.api.user;

import caprita.catalin.cityquest.ui.models.UserModel;
import caprita.catalin.cityquest.ui.models.quest.UserQuestResponseModel;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface that is used by retrofit library to actually generate the methods and map the
 * results to the specified POJOS using GSON mapper*/
public interface UserApi {
    @GET("/api/users/me")
    Flowable<Response<UserModel>> getUserDetails();
    @PATCH("/api/users/me")
    Flowable<Response<UserModel>> updateUserDetails(@Body UpdateUserDto dto);
    @GET("/api/users/me/quests")
    Observable<Response<UserQuestsListingDto>> getUserQuestsById(@Query("status") String status);
    @POST("/api/users/me/quests")
    Observable<Response<Void>> submitUserQuestResponse(@Body UserQuestResponseModel model);
}
