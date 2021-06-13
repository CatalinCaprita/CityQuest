package caprita.catalin.cityquest.ui.dagger.viewmodel;

import androidx.lifecycle.ViewModelProvider;

import caprita.catalin.cityquest.ui.dagger.viewmodel.ViewModelProviderFactory;
import dagger.Binds;
import dagger.Module;

/**
 * Module class that si responsible for creating the ViewModel Factory interface. This will return
 * our implementation of the Factory interface, whenever we need it.*/
@Module
public abstract class ViewModelFactoryModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelProviderFactory);

    /*
        The above method is the same as doing this, but is more efficient, since we are not doing anything
        INSIDE the method. By Saying @Bind instead of @Provides, we tell Dagger to create that interface for
        us, given its Implementation as argument.
    @Provides
     ViewModelProvider.Factory provideViewModelFactory(ViewModelProviderFactory viewModelProviderFactory){
        return viewModelProviderFactory;
    }*/
}
