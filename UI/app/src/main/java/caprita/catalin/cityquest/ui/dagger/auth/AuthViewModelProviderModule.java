package caprita.catalin.cityquest.ui.dagger.auth;

import androidx.lifecycle.ViewModel;

import caprita.catalin.cityquest.ui.auth.AuthViewModel;
import caprita.catalin.cityquest.ui.dagger.viewmodel.ViewModelKey;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class  AuthViewModelProviderModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel.class)
    abstract ViewModel bindAuthViewModel(AuthViewModel authViewModel);
}
