package caprita.catalin.cityquest.ui.main.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.api.ResponseResourceConverter;
import caprita.catalin.cityquest.ui.api.auth.SessionManager;
import caprita.catalin.cityquest.ui.api.user.UpdateUserDto;
import caprita.catalin.cityquest.ui.api.user.UserApi;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserModel;
import caprita.catalin.cityquest.ui.util.Constants;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private static final String TAG = "ProfileViewModel";
    private final UserApi userApi;

    @Inject
    public ProfileViewModel(SessionManager sessionManager,
                            UserApi userApi) {
        this.sessionManager = sessionManager;
        this.userApi = userApi;
    }

    public LiveData<Resource<UserModel>> observeUserModel(){
        return sessionManager.observeUserModel();
    }
    public void queryUserDetails(){
        final LiveData<Resource<UserModel>> source = LiveDataReactiveStreams.fromPublisher(
                userApi.getUserDetails()
                        .onErrorReturn(ResponseResourceConverter::buildResponseFromError)
                        .map(ResponseResourceConverter::buildResourceFromResponse)
                        .subscribeOn(Schedulers.io())
        );
        sessionManager.updateUserModel(source);
    }


    public void attemptLogout() {
        sessionManager.attemptLogout();
    }

    public void updateUser(UpdateUserDto dto) {
        final LiveData<Resource<UserModel>> updateData = LiveDataReactiveStreams.fromPublisher(
                userApi.updateUserDetails(dto)
                        .onErrorReturn(ResponseResourceConverter::buildResponseFromError)
                        .map(ResponseResourceConverter::buildResourceFromResponse)
                .subscribeOn(Schedulers.io())
        );
        sessionManager.updateUserModel(updateData);
    }
//
//    @NonNull
//    private Resource<UserModel> buildResourceFromResponse(@NonNull Response<UserModel> response) throws IOException {
//        if(response.isSuccessful()) {
//            Log.d(TAG, "buildResourceFromResponse: Successful return!");
//            return Resource.success(response.body());
//        }
//        String errm;
//        switch (response.code()){
//            case 500:
//                errm = Constants.Error.SOMETHING_WENT_WRONG;
//                break;
//            case 404:
//                errm = Constants.Error.COULD_NOT_LOCATE_USER;
//                break;
//            default:
//                errm = response.errorBody() != null? response.errorBody().string(): Constants.Error.SOMETHING_WENT_WRONG;
//                break;
//        }
//        return Resource.error(errm, sessionManager.getCurrentUserOrNull());
//    }

}
