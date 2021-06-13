package caprita.catalin.cityquest.ui.main.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import caprita.catalin.cityquest.ui.main.userjournal.UserJournalFragment;

public class FragmentFactoryImpl extends FragmentFactory {
    @NonNull
    @Override
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
        Class<? extends  Fragment> fragmentClass = loadFragmentClass(classLoader, className);

        if(fragmentClass == UserJournalFragment.class) {
            return UserJournalFragment.newInstance();
        }

        return super.instantiate(classLoader, className);
    }
}
