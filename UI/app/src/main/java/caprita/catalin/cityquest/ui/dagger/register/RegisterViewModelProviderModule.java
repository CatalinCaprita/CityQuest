package caprita.catalin.cityquest.ui.dagger.register;

import androidx.lifecycle.ViewModel;

import caprita.catalin.cityquest.ui.dagger.viewmodel.ViewModelKey;
import caprita.catalin.cityquest.ui.register.RegisterViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class RegisterViewModelProviderModule {
    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel.class)
    public abstract ViewModel bindRegisterViewModel(RegisterViewModel viewModel);
}
