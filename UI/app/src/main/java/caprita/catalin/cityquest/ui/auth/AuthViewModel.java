package caprita.catalin.cityquest.ui.auth;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.api.auth.AuthApi;
import caprita.catalin.cityquest.ui.api.auth.LoginRequestDto;
import caprita.catalin.cityquest.ui.api.auth.LoginResponseDto;
import caprita.catalin.cityquest.ui.models.AuthResource;
import caprita.catalin.cityquest.ui.api.auth.SessionManager;
import io.reactivex.schedulers.Schedulers;

public class AuthViewModel extends ViewModel {

    private final AuthApi authApi;
    private final SessionManager sessionManager;
    private static final String TAG = "AuthViewModel";

    @Inject
    public AuthViewModel(AuthApi authApi,
                         SessionManager sessionManager) {
        this.authApi = authApi;
        this.sessionManager = sessionManager;
    }

    public LiveData<AuthResource<LoginResponseDto>> observeLoginResponse() {
        return sessionManager.observeLoginResponse();
    }

    public void attemptLogin(LoginRequestDto loginDto){
        Log.d(TAG, "attemptLogin: Attempting Login.");
        sessionManager.attemptAuthentication(queryWithDto(loginDto));
    }
    public LiveData<AuthResource<LoginResponseDto>> queryWithDto(LoginRequestDto loginDto) {
        final LiveData<AuthResource<LoginResponseDto>> source = LiveDataReactiveStreams.fromPublisher(
                authApi.attemptUserLogin(loginDto)
                        .onErrorReturn(err -> {
                            Log.e(TAG, "attemptLogin: " + err.getMessage());
                            LoginResponseDto responseDto = new LoginResponseDto();
                            responseDto.setId(-1L);
                            responseDto.setToken(null);
                            return responseDto;
                        })
                        .map(responseDto -> {
                            if (responseDto.getToken() == null) {
                                return AuthResource.error("Authentication Failed", (LoginResponseDto) null);
                            }
                            return AuthResource.authenticated(responseDto);
                        })
                        .subscribeOn(Schedulers.io())
        );
        return source;
    }


}
