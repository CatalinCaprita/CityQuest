package caprita.catalin.cityquest.ui.main.userjournal;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.api.auth.LoginResponseDto;
import caprita.catalin.cityquest.ui.main.companion.CompanionAdapter;
import caprita.catalin.cityquest.ui.main.userjournal.rv.QuestBriefAdapter;
import caprita.catalin.cityquest.ui.models.AuthResource;
import caprita.catalin.cityquest.ui.models.quest.QuestBriefModel;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserCompanion;
import caprita.catalin.cityquest.ui.models.UserModel;
import dagger.android.support.DaggerFragment;


public class UserJournalFragment extends DaggerFragment implements CompanionAdapter.OnCompanionClickListener,
        QuestBriefAdapter.OnQuestClickListener {

    private static final String TAG = UserJournalFragment.class.getSimpleName();
    public static final String ARG_DETAILS_QUERIED = "details_queried";

    private TextView titleTextView;
    private TextView tvCompanionHeading;
    private TextView tvCompanionHeading2;
    private final TextView[] tvDayDescriptions = new TextView[3];
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private CardView titleCardView;
    private ShapeableImageView ivProfile;
    private RecyclerView rvCompanions;
    private List<RecyclerView> rvDays = new ArrayList<>();
    private final int[] rvIds = new int[]{R.id.rv_day1, R.id.rv_day2, R.id.rv_day3};
    private RecyclerView.LayoutManager rvLayoutManager;
    private boolean isUserDataQuerried = false;
    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    UserJournalViewModel userJournalViewModel;

    public UserJournalFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserJournalFragment.
     */
    public static UserJournalFragment newInstance() {
        UserJournalFragment fragment = new UserJournalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.isUserDataQuerried = savedInstanceState.getBoolean(ARG_DETAILS_QUERIED);
        }
        setEnterTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.slide_right));
        setExitTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.fade));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_user_journal, container, false);
        return mainView;
    }

    @Override
    public void onSaveInstanceState(@NonNull @io.reactivex.annotations.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_DETAILS_QUERIED, isUserDataQuerried);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = view.findViewById(R.id.tv_title);
        ivProfile = view.findViewById(R.id.iv_profile_pic);

        if (savedInstanceState != null) {
            this.isUserDataQuerried = savedInstanceState.getBoolean(ARG_DETAILS_QUERIED);
            Log.d(TAG, "onViewCreated: should Query User Data ? " + isUserDataQuerried);
        }
        tvCompanionHeading = view.findViewById(R.id.tv_hd_companion);
        tvCompanionHeading2 = view.findViewById(R.id.tv_hd_companion2);
        tvDayDescriptions[0] = view.findViewById(R.id.tv_day1_heading2);
        tvDayDescriptions[1] = view.findViewById(R.id.tv_day2_heading2);
        tvDayDescriptions[2] = view.findViewById(R.id.tv_day3_heading2);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        nestedScrollView = view.findViewById(R.id.scroll_content);

        userJournalViewModel = new ViewModelProvider(this, viewModelProviderFactory)
                .get(UserJournalViewModel.class);

        rvLayoutManager = new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false);
        rvCompanions = view.findViewById(R.id.rv_companions);
        rvCompanions.setAdapter(new CompanionAdapter(Collections.emptyList(), this));
        rvCompanions.setLayoutManager(rvLayoutManager);

        for (int i = 0; i < tvDayDescriptions.length; i++) {
            RecyclerView rvday = view.findViewById(rvIds[i]);
            rvday.setAdapter(new QuestBriefAdapter(Collections.emptyList(), this));
            rvday.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false));
            if (i < rvDays.size() && rvDays.get(i) != null) {
                rvDays.set(i, rvday);
            } else
                rvDays.add(rvday);
        }
        subscribeObservers();

    }

    private void subscribeObservers() {
        userJournalViewModel.observerAuthResource().removeObservers(getViewLifecycleOwner());
        userJournalViewModel.observerAuthResource().observe(getViewLifecycleOwner(),
                new Observer<AuthResource<LoginResponseDto>>() {
                    @Override
                    public void onChanged(AuthResource<LoginResponseDto> loginResponse) {
                        if (loginResponse != null) {
                            switch (loginResponse.status) {
                                case AUTHENTICATED:
                                    Log.d(TAG, "onChanged: User is logged in. Querying data for id");
                                    userJournalViewModel.queryUserDetails();

                                    break;
                                case NOT_AUTHENTICATED:
                                case ERROR:
                                    Toast.makeText(requireContext(), loginResponse.message, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }
                });

        userJournalViewModel.observerUserModelResource().removeObservers(getViewLifecycleOwner());
        userJournalViewModel.observerUserModelResource().observe(getViewLifecycleOwner(),
                new Observer<Resource<UserModel>>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onChanged(Resource<UserModel> userModelResource) {
                        if (userModelResource != null) {
                            switch (userModelResource.status) {
                                case LOADING:
                                    nestedScrollView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    Toast.makeText(requireContext(), "User Data Loading", Toast.LENGTH_SHORT).show();
                                    break;
                                case SUCCESS:
                                    onUserResourceSuccess(userModelResource.data);
                                    Toast.makeText(requireContext(), "User Data Loaded!", Toast.LENGTH_SHORT).show();
                                    userJournalViewModel.queryUserQuests();
                                    break;
                                case ERROR:
                                    progressBar.setVisibility(View.GONE);
                                    Log.e(TAG, "onChanged: " + userModelResource.message);
                                    Toast.makeText(requireContext(), userModelResource.message, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }
                });

        userJournalViewModel.observerUserCompanions().removeObservers(getViewLifecycleOwner());
        userJournalViewModel.observerUserCompanions().observe(getViewLifecycleOwner(),
                new Observer<Resource<List<UserCompanion>>>() {
                    @Override
                    public void onChanged(Resource<List<UserCompanion>> listResource) {
                        if (listResource != null) {
                            Log.d(TAG, "onChanged: List Of Users is working!");
                            if (listResource.data != null) {
                                ((CompanionAdapter) rvCompanions.getAdapter()).setCompanions(listResource.data);
                                rvCompanions.getAdapter().notifyDataSetChanged();

                            }
                        }
                    }
                });

        for (int i = 0; i < rvDays.size(); i++) {
            final int i2 = i;
            userJournalViewModel.observeQuestForDay(i).removeObservers(getViewLifecycleOwner());
            userJournalViewModel.observeQuestForDay(i).observe(getViewLifecycleOwner(),
                    new Observer<Resource<List<QuestBriefModel>>>() {
                        @Override
                        public void onChanged(Resource<List<QuestBriefModel>> listResource) {
                            if (listResource != null &&
                                    listResource.status == Resource.Status.SUCCESS &&
                                    listResource.data != null) {
                                Log.d(TAG, "onChanged: Updating RV For day ");
                                ((QuestBriefAdapter) rvDays.get(i2).getAdapter())
                                        .setQuestBriefs(listResource.data);
                                if (listResource.data.size() < 3) {
                                    tvDayDescriptions[i2].setText(R.string.shd_day_low);
                                } else if (listResource.data.size() < 7) {
                                    tvDayDescriptions[i2].setText(R.string.shd_day_medium);
                                } else {
                                    tvDayDescriptions[i2].setText(R.string.shd_day_high);
                                }

                            }
                        }
                    });
        }

    }

    private void onUserResourceSuccess(UserModel userModel) {
        nestedScrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvCompanionHeading.setText(getString(R.string.hd_journey_begin, userModel.getFirstName(), new Date()));
        if (userModel.getGender().equals("MALE")) {
            tvCompanionHeading2.setText(getString(R.string.hd_companion_gather,
                    "He", "his"));
        } else {
            tvCompanionHeading2.setText(getString(R.string.hd_companion_gather,
                    "She", "her"));
        }
        titleTextView.setText(getString(R.string.title_user_journal, userModel.getFirstName()));
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void onQuestClick(Long position) {

    }
}