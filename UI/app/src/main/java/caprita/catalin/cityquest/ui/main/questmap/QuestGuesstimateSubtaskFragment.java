package caprita.catalin.cityquest.ui.main.questmap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.InvalidPropertiesFormatException;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.PossibleAnswerModel;
import dagger.android.support.DaggerFragment;


public class QuestGuesstimateSubtaskFragment extends DaggerFragment {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    QuestTypeViewModel viewModel;

    private static final String TAG = "QuestGuesstimateSubtaskFragment";
    public static final String ARG_SUBTASK_ID = "param1";
    private SubtaskListener listener;
    private Integer subtaskIndex;
    private Long possibleAnswerId;
    private int minValue = 0;
    private int maxValue = 0;
    private TextInputLayout tvMin, tvMax;
    private FloatingActionButton btnMinusMin, btnPlusMin, btnMinusMax, btnPlusMax;
    private ExtendedFloatingActionButton btnConfirm;
    private MaterialTextView tvSubtaskTitle;

    public QuestGuesstimateSubtaskFragment() {
        // Required empty public constructor
    }

    public static QuestGuesstimateSubtaskFragment newInstance(String param1, String param2) {
        QuestGuesstimateSubtaskFragment fragment = new QuestGuesstimateSubtaskFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subtaskIndex = getArguments().getInt(ARG_SUBTASK_ID);
        }
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.slide_left));
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setAllowEnterTransitionOverlap(true);
        setAllowReturnTransitionOverlap(true);
        assert getParentFragment() != null;
        if(getParentFragment() instanceof  SubtaskListener)
            this.listener = (SubtaskListener) getParentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_guesstimate_subtask, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getParentFragment() != null;
        viewModel = new ViewModelProvider(getParentFragment(), viewModelProviderFactory)
                .get(QuestTypeViewModel.class);

        tvSubtaskTitle = view.findViewById(R.id.tv_subtask_title);
        btnMinusMin = view.findViewById(R.id.btn_minus_min);
        btnMinusMax = view.findViewById(R.id.btn_minus_max);
        btnPlusMax = view.findViewById(R.id.btn_plus_max);
        btnPlusMin = view.findViewById(R.id.btn_plus_min);
        btnConfirm = view.findViewById(R.id.btn_confirm);

        tvMin = view.findViewById(R.id.tv_min_price);
        tvMax = view.findViewById(R.id.tv_max_price);


        btnPlusMin.setOnClickListener(view1 -> {
            minValue = this.addTo(tvMin);
        });
        btnPlusMax.setOnClickListener(view1 -> {
            maxValue = this.addTo(tvMax);
        });
        btnMinusMin.setOnClickListener(view1 -> {
            minValue = this.substractFrom(tvMin);

        });
        btnMinusMax.setOnClickListener(view1 -> {
            maxValue = this.substractFrom(tvMax);
        });
        tvMax.setOnClickListener(view1 -> {
            tvMax.setError(null);
            tvMin.setError(null);
        });
        tvMin.setOnClickListener(view1 -> {
            tvMax.setError(null);
            tvMin.setError(null);
        });
        btnConfirm.setOnClickListener(view1 -> {this.updateAnswer();});
        subscribeObservers();
    }

    private void subscribeObservers() {
        viewModel.observeSubtaskByIndex(subtaskIndex).removeObservers(getViewLifecycleOwner());
        Log.d(TAG, "subscribeObservers: Requesting Task Answers for Subtask" + subtaskIndex);
        viewModel.observeSubtaskByIndex(subtaskIndex).observe(getViewLifecycleOwner(),
                subtaskModelResource -> {
                    switch (subtaskModelResource.status) {
                        case SUCCESS:
                            if (subtaskModelResource.data != null) {
                                tvSubtaskTitle.setText(subtaskModelResource.data.getDescription());
                                PossibleAnswerModel pa = subtaskModelResource.data.getPossibleAnswers().get(0);
                                possibleAnswerId = pa.getId();
                            }
                            tvMax.getEditText().setText(Integer.toString(maxValue));
                            tvMin.getEditText().setText(Integer.toString(minValue));
                            break;
                    }
                }
        );
    }

    private void updateAnswer(){
        boolean validInput = true;
        if(TextUtils.isEmpty(tvMax.getEditText().getText())) {
            validInput = false;
            tvMax.setErrorEnabled(true);
            tvMax.setError(getString(R.string.err_empty));
        }
        if(TextUtils.isEmpty(tvMin.getEditText().getText())) {
            validInput = false;
            tvMax.setErrorEnabled(true);
            tvMax.setError(getString(R.string.err_empty));
        }
        try {
            maxValue = Integer.parseInt(tvMax.getEditText().getText().toString());
            minValue = Integer.parseInt(tvMin.getEditText().getText().toString());
            if (minValue > maxValue)
                throw new InvalidPropertiesFormatException("nothing");
        } catch (Exception e) {
            validInput = false;
            tvMax.setErrorEnabled(true);
            tvMin.setErrorEnabled(true);
            tvMax.setError(getString(R.string.err_number_format));
            tvMin.setError(getString(R.string.err_number_format));
        }
        if(validInput){
            String value = getString(R.string.guesstimate_range_template,minValue, maxValue);
            Log.d(TAG, "updateAnswer: Updating For Subtask index: " + subtaskIndex + " value: " + value );
            tvMax.setError(null);
            tvMin.setError(null);
            listener.onSubtaskUpdate(possibleAnswerId, value);
        }
    }
    private int substractFrom(TextInputLayout tvPrice) {
        try {
            int next;
            if(tvPrice.getEditText().getText() != null){
                String currentValue = tvPrice.getEditText().getText().toString();
                next = Math.max(Integer.parseInt(currentValue) - 1, 0);
            }else{
                next = 0;
            }
            tvPrice.getEditText().setText(Integer.toString(next));
            return next;
        }catch (NumberFormatException | NullPointerException e){
            Log.e(TAG, "substractFrom: " + e.getMessage());
            return 0;
        }
    }

    private int addTo(TextInputLayout tvPrice) {
        try {
            Integer next;
            if(tvPrice.getEditText().getText() != null){
                String currentValue = tvPrice.getEditText().getText().toString();
                next = Integer.parseInt(currentValue) + 1;
            }else{
                next = 1;
            }
            tvPrice.getEditText().setText(next.toString());
            return next;
        }catch (NumberFormatException | NullPointerException e){
            Log.e(TAG, "addTo: " + e.getMessage());
            return 0;
        }
    }

}