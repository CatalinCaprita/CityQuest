package caprita.catalin.cityquest.ui.dagger;

import android.app.Application;

import javax.inject.Singleton;

import caprita.catalin.cityquest.ui.BaseApplication;
import caprita.catalin.cityquest.ui.api.auth.AuthorizationTokenInterceptor;
import caprita.catalin.cityquest.ui.dagger.viewmodel.ViewModelFactoryModule;
import caprita.catalin.cityquest.ui.api.auth.SessionManager;
import caprita.catalin.cityquest.ui.util.QuestClusterManagerRenderer;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityBuildersModule.class,
        ViewModelFactoryModule.class,
        AppModule.class
})
@Singleton
 /*Base application is going to be a client of this AppComponent, aka it will
* create instances of classes by using this component.Skips the definition of "inject" methods*/
public interface AppComponent extends AndroidInjector<BaseApplication> {

    SessionManager sessionManager();
    AuthorizationTokenInterceptor authorizationTokenInterceptor();
    /*Override the builder*/
    @Component.Builder
     interface Builder{

        @BindsInstance
        Builder application(Application application);

        AppComponent build();

    }
}
