package caprita.catalin.cityquest.ui.api.auth;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import caprita.catalin.cityquest.ui.api.auth.LoginRequestDto;
import caprita.catalin.cityquest.ui.api.auth.LoginResponseDto;
import caprita.catalin.cityquest.ui.models.AuthResource;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserCompanion;
import caprita.catalin.cityquest.ui.models.UserModel;

@Singleton
public class SessionManager {
    private final MediatorLiveData<AuthResource<LoginResponseDto>> loginMediator = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<UserModel>> userModel = new MediatorLiveData<>();
    private static final String TAG = "SessionManager";
    private String sessionToken;
    @Inject
    public SessionManager() {
    }

    /**
     * This method will be used to globally keep track of the current status of the authentication
     * AuthResource is a wrapper around the response dto from the login. And the resource could be
     * reacted to depeding on its status.
     * The Auth ViewModel will attempt to use the Auth Api and return the source of the api call here.*/
    public void attemptAuthentication(final LiveData<AuthResource<LoginResponseDto>> source){
        loginMediator.setValue(AuthResource.loading(getCurrentLoginOrNull()));

        loginMediator.addSource(source, new Observer<AuthResource<LoginResponseDto>>() {
            @Override
            public void onChanged(AuthResource<LoginResponseDto> loginResponseDtoAuthResource) {
                if(loginResponseDtoAuthResource != null) {
                    Log.d(TAG, "onChanged: Setting the Response Dto in data!");
                    if(loginResponseDtoAuthResource.status == AuthResource.AuthStatus.AUTHENTICATED) {
                        sessionToken = loginResponseDtoAuthResource.data.getToken();
                    }
                    loginMediator.setValue(loginResponseDtoAuthResource);
                    loginMediator.removeSource(source);
                }
            }
        });
    }

    public void initUserModel(final LiveData<Resource<UserModel>> source){
        userModel.setValue(Resource.loading(getCurrentUserOrNull()));
            userModel.addSource(source, new Observer<Resource<UserModel>>() {
                @Override
                public void onChanged(Resource<UserModel> userModelResource) {
                    if(userModelResource != null){
                        userModel.setValue(userModelResource);
                    userModel.removeSource(source);
                    }
                }
            });
    }
    public void updateUserModel(final LiveData<Resource<UserModel>> source){
        userModel.setValue(Resource.loading(getCurrentUserOrNull()));
        try{
        userModel.addSource(source, new Observer<Resource<UserModel>>() {
            @Override
            public void onChanged(Resource<UserModel> userModelResource) {
                if(userModelResource != null){
                    userModel.setValue(userModelResource);
                    userModel.removeSource(source);
                }
            }
        });
        }catch (IllegalArgumentException e){
            Log.e(TAG, "updateUserModel: Already present source.");
        }
    }

    public void addUserQuestCount(int finishedQuests){
        if(userModel.getValue() != null && userModel.getValue().data != null) {
            userModel.getValue().data.setTotalFinishedQuests(finishedQuests);
//            userModel.setValue(userModel.getValue());
            Log.d(TAG, "addUserQuestCount: Total user Finished Quests: " + finishedQuests);
        }else{
            Log.e(TAG, "addUserQuestCount: Null user model or data!");
        }
    }
    public MediatorLiveData<Resource<UserModel>> observeUserModel(){
        return this.userModel;
    }

    public void attemptLogout(){
        Log.d(TAG, "attemptLogout: Attempting Logout.");
        loginMediator.setValue(AuthResource.logout());
        sessionToken = null;
    }

    public LiveData<AuthResource<LoginResponseDto>> observeLoginResponse(){
        return this.loginMediator;
    }

    public UserModel getCurrentUserOrNull(){
        return this.userModel.getValue() != null ? this.userModel.getValue().data : null;
    }
    public LoginResponseDto getCurrentLoginOrNull(){
        return this.loginMediator.getValue() != null ? this.loginMediator.getValue().data : null;
    }
}
