package caprita.catalin.cityquest.ui.api.auth;

import android.util.Log;

import androidx.lifecycle.Observer;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import caprita.catalin.cityquest.ui.models.AuthResource;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class AuthorizationTokenInterceptor implements Interceptor {

    private final SessionManager sessionManager;
    private String token = "";
    private static final String TAG = "AuthorizationTokenInterceptor";
    private static final String BEARER_TEMPLATE = "Bearer %s";
    @Inject
    public AuthorizationTokenInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        /*
        * Main purpose is to observe the session manager auth resource forever*/
        sessionManager.observeLoginResponse().observeForever(new Observer<AuthResource<LoginResponseDto>>() {
            @Override
            public void onChanged(AuthResource<LoginResponseDto> loginResource) {
                if(loginResource != null && loginResource.status == AuthResource.AuthStatus.AUTHENTICATED){
                    Log.d(TAG, "onChanged: Setting new authorization token!");
                    token = loginResource.data.getToken();
                }
            }
        });
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.d(TAG, "intercept: Adding Non empty token ?:  " + !token.isEmpty());
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Authorization", String.format(BEARER_TEMPLATE, token));
        return chain.proceed(builder.build());
    }
}
