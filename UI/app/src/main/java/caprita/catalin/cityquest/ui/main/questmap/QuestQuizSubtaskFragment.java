package caprita.catalin.cityquest.ui.main.questmap;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.PossibleAnswerModel;
import dagger.android.support.DaggerFragment;


public class QuestQuizSubtaskFragment extends DaggerFragment {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    QuestTypeViewModel viewModel;

    private static final String TAG = "QuestQuizSubtaskFragmen";
    public static final String KEY_SUBTASK_ID = "param1";

    private Integer subtaskIndex;
    private final List<RadioButton> radioButtons = new ArrayList<>(4);
    private final List<Long> paIds = new ArrayList<>(4);
    private RadioGroup radioGroup;
    private MaterialTextView tvSubtaskTitle;
    private SubtaskListener listener;

    public QuestQuizSubtaskFragment() {
        // Required empty public constructor
    }

    public static QuestQuizSubtaskFragment newInstance() {
        return new QuestQuizSubtaskFragment();
    }

    public SubtaskListener getListener() {
        return listener;
    }

    public void setListener(SubtaskListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subtaskIndex = getArguments().getInt(KEY_SUBTASK_ID);
        }
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.slide_left));
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setAllowEnterTransitionOverlap(true);
        setAllowReturnTransitionOverlap(true);
        if (this.getParentFragment() instanceof SubtaskListener)
            this.listener = (SubtaskListener) getParentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_quiz_subtask, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getParentFragment() != null;
        viewModel = new ViewModelProvider(getParentFragment(), viewModelProviderFactory)
                .get(QuestTypeViewModel.class);
        tvSubtaskTitle = view.findViewById(R.id.tv_subtask_title);

        radioGroup = view.findViewById(R.id.radio_group);
        if(radioButtons.isEmpty()) {
            radioButtons.add(view.findViewById(R.id.r1));
            radioButtons.add(view.findViewById(R.id.r2));
            radioButtons.add(view.findViewById(R.id.r3));
            radioButtons.add(view.findViewById(R.id.r4));
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int rid) {
                for (int i = 0; i < radioButtons.size(); i++) {
                    if (radioButtons.get(i).getId() == rid) {
                        listener.onSubtaskUpdate(paIds.get(i));
                        break;
                    }
                }
            }
        });
        subscribeObservers();

    }

    @SuppressLint("ResourceAsColor")
    private void subscribeObservers() {
        viewModel.observeSubtaskByIndex(subtaskIndex).removeObservers(getViewLifecycleOwner());
        Log.d(TAG, "subscribeObservers: Requesting Task Answers for Subtask " + subtaskIndex);
        viewModel.observeSubtaskByIndex(subtaskIndex).observe(getViewLifecycleOwner(),
                subtaskModelResource -> {
                    switch (subtaskModelResource.status) {
                        case ERROR:
                            listener.onSubtaskError();
                            break;
                        case SUCCESS:
                            if (subtaskModelResource.data != null) {
                                tvSubtaskTitle.setText(subtaskModelResource.data.getDescription());
                                final List<PossibleAnswerModel> pas = subtaskModelResource.data.getPossibleAnswers();
                                boolean shouldAdd = paIds.isEmpty();
                                for (int i = 0; i < pas.size(); i++) {
                                    if(shouldAdd) {
                                        paIds.add(i, pas.get(i).getId());
                                        radioButtons.get(i).setText(pas.get(i).getContent());
                                    }
                                    else {
                                        paIds.set(i, pas.get(i).getId());
                                    }
                                }
                            }
                            break;
                    }
                }
        );
    }
}