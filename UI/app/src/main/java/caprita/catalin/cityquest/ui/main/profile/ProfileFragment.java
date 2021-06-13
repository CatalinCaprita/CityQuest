package caprita.catalin.cityquest.ui.main.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserModel;
import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends DaggerFragment implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "ProfileFragment";

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    ProfileViewModel viewModel;

    private MaterialTextView tvUsername;
    private ShapeableImageView ivProfile;
    private FloatingActionButton btnSettings;
    private TabLayout tabLayout;
    private FragmentContainerView containerView;
    private FragmentManager fragmentManager;
    private ProgressBar indicator;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername = view.findViewById(R.id.tv_username);
        ivProfile = view.findViewById(R.id.iv_quest_location);
        indicator = view.findViewById(R.id.progress_bar);
        btnSettings = view.findViewById(R.id.btn_settings);

        btnSettings.setOnClickListener(this::showMenu);
        containerView = view.findViewById(R.id.fragment_container_view);
        fragmentManager = getChildFragmentManager();
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory)
                .get(ProfileViewModel.class);

        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.getTabAt(0).setText(getString(R.string.profile_tab_credentials));
        tabLayout.getTabAt(1).setText(getString(R.string.profile_tab_stats));
        tabLayout.getTabAt(0).setContentDescription(getString(R.string.profile_tab_credentials));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Class< ? extends  DaggerFragment> next;
                if(tab.getText().equals(getString(R.string.profile_tab_credentials)))
                    next = UserInfoFragment.class;
                else
                    next = StatsFragment.class;
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, next, null, next.getSimpleName())
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        subscribeObservers();
    }

    private void subscribeObservers() {
        viewModel.observeUserModel().removeObservers(getViewLifecycleOwner());
        viewModel.observeUserModel().observe(getViewLifecycleOwner(),
                new Observer<Resource<UserModel>>() {
                    @Override
                    public void onChanged(Resource<UserModel> resource) {
                        if(resource != null){
                            switch (resource.status){
                                case LOADING:
                                    indicator.setVisibility(View.VISIBLE);
                                    containerView.setVisibility(View.GONE);
                                    tvUsername.setText(getString(R.string.state_loading));
                                    break;
                                case ERROR:
                                    indicator.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_LONG).show();
                                case SUCCESS:
                                    indicator.setVisibility(View.GONE);
                                    containerView.setVisibility(View.VISIBLE);
                                    if(resource.data != null){
                                        tvUsername.setText(resource.data.getUsername());
                                        if(fragmentManager.getBackStackEntryCount() == 0){
                                            fragmentManager.beginTransaction()
                                                    .replace(R.id.fragment_container_view, UserInfoFragment.class,
                                                            null,
                                                            null)
                                                    .commit();
                                        }
                                    }
                            }
                        }
                    }
                });
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(this.getContext(), v);
        popupMenu.inflate(R.menu.user_profile_menu);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.popup_item_edit:
                Bundle bundle = new Bundle();
                bundle.putBoolean(UserInfoFragment.KEY_EDIT_MODE, true);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, UserInfoFragment.class, bundle,null)
                        .commit();
                break;
            case R.id.popup_item_logout:
                viewModel.attemptLogout();
                break;
        }
        return true;
    }
}