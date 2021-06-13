package caprita.catalin.cityquest.ui.dagger.main;

import caprita.catalin.cityquest.ui.api.quest.QuestApi;
import caprita.catalin.cityquest.ui.api.user.UserApi;
import caprita.catalin.cityquest.ui.services.LocationService;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class MainModule {
    @Provides
    public static UserApi provideUserApi(Retrofit retrofit){
        return retrofit.create(UserApi.class);
    }
    @Provides
    public static QuestApi provideQuestApi(Retrofit retrofit){
        return retrofit.create(QuestApi.class);
    }

}
