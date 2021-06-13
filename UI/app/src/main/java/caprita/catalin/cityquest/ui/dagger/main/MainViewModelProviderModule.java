package caprita.catalin.cityquest.ui.dagger.main;

import androidx.lifecycle.ViewModel;

import caprita.catalin.cityquest.ui.dagger.viewmodel.ViewModelKey;
import caprita.catalin.cityquest.ui.main.profile.ProfileViewModel;
import caprita.catalin.cityquest.ui.main.questmap.QuestMapFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestMapViewModel;
import caprita.catalin.cityquest.ui.main.questmap.QuestTypeViewModel;
import caprita.catalin.cityquest.ui.main.userjournal.UserJournalViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract  class MainViewModelProviderModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserJournalViewModel.class)
    public abstract ViewModel bindUserJournalViewModel(UserJournalViewModel userJournalViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    public abstract ViewModel bindProfileViewModel(ProfileViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(QuestMapViewModel.class)
    public abstract ViewModel bindQuestMapViewModel(QuestMapViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(QuestTypeViewModel.class)
    public abstract ViewModel bindQuestTypeViewModel(QuestTypeViewModel viewModel);


}
