package caprita.catalin.cityquest.ui.dagger;

import caprita.catalin.cityquest.ui.auth.AuthActivity;
import caprita.catalin.cityquest.ui.dagger.auth.AuthModule;
import caprita.catalin.cityquest.ui.dagger.auth.AuthViewModelProviderModule;
import caprita.catalin.cityquest.ui.dagger.main.MainActivityFragmentsModule;
import caprita.catalin.cityquest.ui.dagger.main.MainModule;
import caprita.catalin.cityquest.ui.dagger.main.MainViewModelProviderModule;
import caprita.catalin.cityquest.ui.dagger.register.RegisterFragmentBuildersModule;
import caprita.catalin.cityquest.ui.dagger.register.RegisterModule;
import caprita.catalin.cityquest.ui.dagger.register.RegisterViewModelProviderModule;
import caprita.catalin.cityquest.ui.main.MainActivity;
import caprita.catalin.cityquest.ui.register.RegisterActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/*
* Usually Abstract classes when using @ContributeAndroidInjector*/
@Module
public abstract class ActivityBuildersModule {

    /*
       Basically We let Dagger know this is a potential AppComponent Client that will use
    * the dependecies Injected and managed by AppComponent
    * This annotation actualyy constructs a Subcomponent of the AppComponent, that is with scope
    * of the AuthActivity.
    * */

    @ContributesAndroidInjector(
            modules = {
                    MainModule.class,
                    MainViewModelProviderModule.class,
                    MainActivityFragmentsModule.class
            }
    )
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(
            modules = {AuthModule.class, AuthViewModelProviderModule.class}
    )
    abstract AuthActivity contributeAuthActivity();

    @ContributesAndroidInjector(
        modules = {RegisterModule.class,
                RegisterFragmentBuildersModule.class,
                RegisterViewModelProviderModule.class
        }
    )
    abstract RegisterActivity contributeRegisterActivity();

}
