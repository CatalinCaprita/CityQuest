package caprita.catalin.cityquest.ui.dagger.main;

import caprita.catalin.cityquest.ui.main.profile.ProfileFragment;
import caprita.catalin.cityquest.ui.main.profile.StatsFragment;
import caprita.catalin.cityquest.ui.main.profile.UserInfoFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestGuesstimateFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestGuesstimateSubtaskFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestMapFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestPrepareFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestQuizSubtaskFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestQuizFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestResultsFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestSnsFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestSnsSubtasksFragment;
import caprita.catalin.cityquest.ui.main.userjournal.UserJournalFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityFragmentsModule {
    @ContributesAndroidInjector
    abstract UserJournalFragment contributeUserJournalFragment();
    @ContributesAndroidInjector
    abstract ProfileFragment contributeProfileFragment();
    @ContributesAndroidInjector
    abstract UserInfoFragment contributeUserInfoFragment();
    @ContributesAndroidInjector
    abstract StatsFragment contributeStatsFragment();
    @ContributesAndroidInjector
    abstract QuestMapFragment contributeQuestMapFragment();
    @ContributesAndroidInjector
    abstract QuestQuizFragment contributeQuestQuizFragment();
    @ContributesAndroidInjector
    abstract QuestPrepareFragment contributeQuestPrepareFragment();
    @ContributesAndroidInjector
    abstract QuestResultsFragment contributeQuestResultsFragment();
    @ContributesAndroidInjector
    abstract QuestQuizSubtaskFragment contributeQuestQuizChildFragment();
    @ContributesAndroidInjector
    abstract QuestSnsFragment contributeQuestSnsFragment();
    @ContributesAndroidInjector
    abstract QuestSnsSubtasksFragment contributeQuestSnsSubtasksFragment();
    @ContributesAndroidInjector
    abstract QuestGuesstimateFragment contributeQuestGeusstimateFragment();
    @ContributesAndroidInjector
    abstract QuestGuesstimateSubtaskFragment contributeQuestGuesstimateSubtaskFragment();



}
