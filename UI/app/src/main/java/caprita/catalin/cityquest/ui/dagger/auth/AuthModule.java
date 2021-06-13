package caprita.catalin.cityquest.ui.dagger.auth;

import caprita.catalin.cityquest.ui.api.auth.AuthApi;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Module Where we are going to add all dependencies within the AuthScope*/
@Module
public class  AuthModule {
    @Provides
    static AuthApi provideAuthApi(Retrofit retrofit){
        return retrofit.create(AuthApi.class);
    }
}
