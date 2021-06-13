package caprita.catalin.cityquest.ui.api.auth;
import caprita.catalin.cityquest.ui.models.UserModel;
import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthApi {
    @POST("/api/users/login")
    Flowable<LoginResponseDto> attemptUserLogin(@Body LoginRequestDto loginRequestDto);
}
