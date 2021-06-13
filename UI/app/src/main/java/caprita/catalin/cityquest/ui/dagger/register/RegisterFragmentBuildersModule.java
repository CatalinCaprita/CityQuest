package caprita.catalin.cityquest.ui.dagger.register;

import caprita.catalin.cityquest.ui.register.CompanionsFragment;
import caprita.catalin.cityquest.ui.register.CredentialsFragment;
import caprita.catalin.cityquest.ui.register.PersonalDataFragment;
import caprita.catalin.cityquest.ui.register.PersonalityQuizFragment;
import caprita.catalin.cityquest.ui.register.QuestionChildFragment;
import caprita.catalin.cityquest.ui.register.WaitFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class RegisterFragmentBuildersModule {

    @ContributesAndroidInjector
    public abstract CredentialsFragment provideCredentialsFragment();
    @ContributesAndroidInjector
    public abstract PersonalDataFragment providePersonalDataFragment();
    @ContributesAndroidInjector
    public abstract PersonalityQuizFragment providePersonalityQuizFragment();
    @ContributesAndroidInjector
    public abstract QuestionChildFragment provideQuestionChildFragment();
    @ContributesAndroidInjector
    public abstract WaitFragment provideWaitFragments();
    @ContributesAndroidInjector
    public abstract CompanionsFragment provideAddCompanionsFragment();


}
