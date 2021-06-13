package caprita.catalin.cityquest.ui.main.questmap;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import java.util.Collections;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.main.OnFullScreenRequestListener;
import caprita.catalin.cityquest.ui.main.questmap.rv.SnsSubtaskResultAdapter;
import caprita.catalin.cityquest.ui.main.questmap.rv.SubtaskResultAdapter;
import caprita.catalin.cityquest.ui.models.enums.QuestType;
import dagger.android.support.DaggerFragment;


public class QuestResultsFragment extends DaggerFragment {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    QuestTypeViewModel viewModel;

    public static final String ARG_QUEST_TYPE = "arg_quest_type";
    private RecyclerView rvResults;
    private MaterialTextView tvQuestTitle, tvTitleResults, tvError;
    private MaterialTextView tvTravelerTitle, tvPrimary, tvSecondary;
    private QuestType questType;
    private OnFullScreenRequestListener fullScreenRequestListener;
    public QuestResultsFragment() {
    }

    public static QuestResultsFragment newInstance(String param1, String param2) {
        QuestResultsFragment fragment = new QuestResultsFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof  OnFullScreenRequestListener){
            this.fullScreenRequestListener = (OnFullScreenRequestListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.questType = QuestType.valueOf(getArguments().getString(ARG_QUEST_TYPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getParentFragment() != null;
        viewModel = new ViewModelProvider(getParentFragment(), viewModelProviderFactory)
                .get(QuestTypeViewModel.class);

        tvQuestTitle = view.findViewById(R.id.tv_quest_title);
        tvTitleResults = view.findViewById(R.id.tv_title_results);
        tvError = view.findViewById(R.id.tv_error);
        tvError.setVisibility(View.GONE);
        rvResults = view.findViewById(R.id.rv_results);
        rvResults.setHasFixedSize(true);
        if(this.fullScreenRequestListener == null
                && requireActivity() instanceof OnFullScreenRequestListener){
            this.fullScreenRequestListener = (OnFullScreenRequestListener) requireActivity();
        }
        switch (questType){
            case GUESSTIMATE:
            case QUIZ:
                rvResults.setAdapter(new SubtaskResultAdapter(Collections.emptyList()));
                rvResults.setLayoutManager( new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false));
                break;
            case STROLL_AND_SEE:
                rvResults.setAdapter(new SnsSubtaskResultAdapter(Collections.emptyList()));
                rvResults.setLayoutManager( new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false));
                break;
        }
        tvTravelerTitle = view.findViewById(R.id.tv_title_traveler_traits);
        tvPrimary = view.findViewById(R.id.tv_primary_rwd);
        tvSecondary = view.findViewById(R.id.tv_secondary_rwd);
        subscribeObservers();
    }

    private void subscribeObservers() {
        viewModel.observeQuestResult().removeObservers(getViewLifecycleOwner());
        viewModel.observeQuestResult().observe(getViewLifecycleOwner(),
                resultResource -> {
                        switch (resultResource.status){
                            case ERROR:
                                try {
                                    int message = Integer.parseInt(resultResource.message);
                                    if (message == 404) {
                                        tvError.setText(R.string.err_results_404);
                                    } else {
                                        tvError.setText(R.string.err_500);
                                    }

                                }catch(NumberFormatException ex){
                                    tvError.setText(R.string.err_results_not_http);
                                }
                                break;
                            case SUCCESS:
                                if(resultResource.data != null) {
                                    if(rvResults.getAdapter() instanceof  SubtaskResultAdapter) {
                                        ((SubtaskResultAdapter) rvResults.getAdapter()).setResults(
                                                resultResource.data.getResults()
                                        );
                                    }
                                    else {
                                        ((SnsSubtaskResultAdapter) rvResults.getAdapter()).setResults(
                                                resultResource.data.getResults()
                                        );
                                    }
                                    tvPrimary.setText(getString(R.string.quest_card_rwd_template,
                                            resultResource.data.getPrimaryRewardAmount(),
                                            resultResource.data.getPrimaryRewardType()
                                            ));
                                    tvSecondary.setText(getString(R.string.quest_card_rwd_template,
                                            resultResource.data.getSecondaryRewardAmount(),
                                            resultResource.data.getSecondaryRewardType()
                                    ));

                                }
                        }

                });
        viewModel.observeQuest().removeObservers(getViewLifecycleOwner());
        viewModel.observeQuest().observe(getViewLifecycleOwner(),
                questModelResource -> {
                    switch (questModelResource.status){
                        case ERROR:
                            rvResults.setVisibility(View.GONE);
                            tvError.setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            if(questModelResource.data != null) {
                                rvResults.setVisibility(View.VISIBLE);
                                tvQuestTitle.setText(questModelResource.data.getTitle());
                                break;
                            }
                    }
                });

    }
}