package caprita.catalin.cityquest.ui.dagger.register;

import caprita.catalin.cityquest.ui.api.PredictionsApi;
import caprita.catalin.cityquest.ui.api.auth.AuthApi;
import caprita.catalin.cityquest.ui.api.register.RegisterApi;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class RegisterModule {
    @Provides
    public static RegisterApi provideRegisterApi(Retrofit retrofit){
        return retrofit.create(RegisterApi.class);
    }
    @Provides
    public static AuthApi provideAuthApi(Retrofit retrofit){
        return retrofit.create(AuthApi.class);
    }

}
