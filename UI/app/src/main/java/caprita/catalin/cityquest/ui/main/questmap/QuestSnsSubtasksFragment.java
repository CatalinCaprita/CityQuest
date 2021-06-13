package caprita.catalin.cityquest.ui.main.questmap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.main.questmap.rv.SnsSubtaskAdapter;
import caprita.catalin.cityquest.ui.models.PossibleAnswerModel;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.quest.SnsSubtaskModel;
import dagger.android.support.DaggerFragment;


public class QuestSnsSubtasksFragment extends DaggerFragment implements SnsSubtaskAdapter.OnSubtaskCheckedListener{

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    QuestTypeViewModel viewModel;
    RecyclerView rvSubtasks;
    private static final String TAG = "QuestSnsSubtasksFragmen";
    public QuestSnsSubtasksFragment() {
        // Required empty public constructor
    }

    public static QuestSnsSubtasksFragment newInstance(String param1, String param2) {
        QuestSnsSubtasksFragment fragment = new QuestSnsSubtasksFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_sns_subtasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvSubtasks = view.findViewById(R.id.rv_to_check);
        rvSubtasks.setLayoutManager( new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        assert getParentFragment() != null;
        viewModel = new ViewModelProvider(getParentFragment(), viewModelProviderFactory)
                .get(QuestTypeViewModel.class);
        subscribeObservers();
    }

    private void subscribeObservers() {
        this.viewModel.observeQuest().removeObservers(getViewLifecycleOwner());
        this.viewModel.observeQuest().observe(getViewLifecycleOwner(),
                questModelResource -> {
                    if(questModelResource.status == Resource.Status.SUCCESS &&
                    questModelResource.data != null){
                        List<SnsSubtaskModel> snsModels = questModelResource.data.getSubtasks()
                                .stream()
                                .map(subtaskModel -> {
                                    SnsSubtaskModel  model = new SnsSubtaskModel();
                                    model.setSubtaskContent(subtaskModel.getDescription());
                                    model.setSubtaskId(subtaskModel.getId());
                                    PossibleAnswerModel pa = subtaskModel.getPossibleAnswers().get(0);
                                    model.setUniqueAnswerId(pa.getId());
                                    model.setUniqueAnswerContent(pa.getContent());
                                    model.setUserAnswerId(-1L);
                                    return model;
                                }).collect(Collectors.toList());
                        rvSubtasks.setAdapter( new SnsSubtaskAdapter(snsModels, this));
                    }
                });
    }

    @Override
    public void onSubtaskChecked(int subtaskIndex, Long uniqueAnswerId) {
        Log.d(TAG, "onSubtaskChecked: Checking for subtaskIndex " + subtaskIndex + " with positive answer");
        viewModel.completeSubtaskWithAnswer(subtaskIndex,uniqueAnswerId);
    }

    @Override
    public void onSubtaskUnchecked(int subtaskIndex) {
        Log.d(TAG, "onSubtaskChecked: Unchecking for subtaskIndex " + subtaskIndex + " with positive answer");
        viewModel.completeSubtaskWithAnswer(subtaskIndex,-1L);
    }
}