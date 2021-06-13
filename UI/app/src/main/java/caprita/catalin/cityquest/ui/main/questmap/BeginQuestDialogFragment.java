package caprita.catalin.cityquest.ui.main.questmap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.quest.QuestBriefModel;
import caprita.catalin.cityquest.ui.models.enums.QuestType;


public class BeginQuestDialogFragment extends DialogFragment {
   private static final String ARG_QUEST_ID = "quest_id";
    private static final String ARG_QUEST_TYPE = "quest_string_type";

    private OnStartClickListener listener;
    private Long questId;
    private QuestType questType;

    public interface OnStartClickListener{
        void onStartClick(Long questId, QuestType questType);
    }
    public BeginQuestDialogFragment() {
        // Required empty public constructor
    }

    public BeginQuestDialogFragment(OnStartClickListener listener) {
        this.listener = listener;
    }

    public void setListener(OnStartClickListener listener){
        this.listener = listener;
    }

    public static BeginQuestDialogFragment newInstance(Long questId, String questType) {
        BeginQuestDialogFragment fragment = new BeginQuestDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_QUEST_ID, questId);
        args.putString(ARG_QUEST_TYPE, questType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            questId = getArguments().getLong(ARG_QUEST_ID);
            questType = QuestType.valueOf(getArguments().getString(ARG_QUEST_TYPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_begin_quest_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View itemView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(itemView, savedInstanceState);

        MaterialButton btnConfirm = itemView.findViewById(R.id.btn_start);
        btnConfirm.setOnClickListener(view1 ->{
            this.listener.onStartClick(questId, questType);
            dismiss();
        });
        MaterialButton btnCancel = itemView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(view1 ->{
            dismiss();
        });
    }
}