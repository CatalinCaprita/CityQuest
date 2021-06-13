package caprita.catalin.cityquest.ui.main.questmap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.enums.QuestType;
import dagger.android.support.DaggerFragment;

public class QuestPrepareFragment extends DaggerFragment {

    private static final String TAG = "QuestPrepareFragment";
    public static final String QUEST_TITLE_KEY = "quest_title";
    public static final String QUEST_DURATION_KEY = "quest_duration";
    public static final String QUEST_TYPE_KEY  = "quest_type";
    private String questTitle;
    private int questDuration;
    private QuestType questType;
    private MaterialTextView tvQuestTitle,tvTimer, tvQuestTypeDesc;
    private OnUnderstoodListener listener;
    public QuestPrepareFragment() {
        // Required empty public constructor
    }

    public interface OnUnderstoodListener{
        void onUnderstood();
    }
    public static QuestPrepareFragment newInstance() {
        QuestPrepareFragment fragment = new QuestPrepareFragment();
//        Bundle args = new Bundle();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            questTitle = getArguments().getString(QUEST_TITLE_KEY);
            questDuration = getArguments().getInt(QUEST_DURATION_KEY);
            questType = QuestType.valueOf(getArguments().getString(QUEST_TYPE_KEY));
        }
        if(getParentFragment() != null && getParentFragment() instanceof OnUnderstoodListener){
            Log.d(TAG, "onCreate: Attached Listener");
            this.listener = (OnUnderstoodListener) getParentFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_prepare, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvQuestTitle = view.findViewById(R.id.tv_quest_title);
        tvTimer = view.findViewById(R.id.tv_timer);
        tvTimer.setText(getString(R.string.quest_timer_template, questDuration));
        tvQuestTitle.setText(questTitle);
        tvQuestTypeDesc = view.findViewById(R.id.tv_quest_type_desc);
        int descId;
        switch (questType){
            case QUIZ:
                descId = R.string.quest_quiz_description;
                break;
            case GUESSTIMATE:
                descId = R.string.quest_guesstimate_description;
                break;
            default:
                descId = R.string.quest_stroll_and_see_description;
                break;
        }
        tvQuestTypeDesc.setText(getString(descId));
    }
}