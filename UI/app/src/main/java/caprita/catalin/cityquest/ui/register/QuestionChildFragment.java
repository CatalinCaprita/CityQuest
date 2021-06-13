package caprita.catalin.cityquest.ui.register;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.security.InvalidParameterException;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.BfpiQuestion;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserModel;
import dagger.android.support.DaggerFragment;

public class QuestionChildFragment extends DaggerFragment {

    private RadioGroup radioGroup;
    private ExtendedFloatingActionButton btnNext, btnPrev;
    private LinearProgressIndicator progressIndicator;
    private MaterialTextView tvQuestion;
    private int questionId = -1;
    private int checkId = -1;

    ProgressBar progressBar;
    private static final String TAG = "QuestionChildFragment";
    private static final int[] CHECK_IDS = {
            R.id.r1,
            R.id.r2,
            R.id.r3,
            R.id.r4,
            R.id.r5
    };
    public static final String QUESTION_ID_KEY = "arg1";

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    RegisterViewModel viewModel;

    private OnRegistrationStepChangedListener listener;

    public QuestionChildFragment() {
    }


    public static QuestionChildFragment newInstance() {
        QuestionChildFragment fragment = new QuestionChildFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(OnRegistrationStepChangedListener.class.isAssignableFrom(context.getClass())) {
            Log.d(TAG, "onAttach: Setting up listener");
            this.listener = (OnRegistrationStepChangedListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            questionId = getArguments().getInt(QUESTION_ID_KEY);
            Log.d(TAG, "onCreate: Fragment For question: " + questionId);
        }
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.slide_left));
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setAllowEnterTransitionOverlap(true);
        setAllowReturnTransitionOverlap(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question_child, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory)
                .get(RegisterViewModel.class);
        radioGroup = view.findViewById(R.id.radio_group);
        btnNext = view.findViewById(R.id.btn_next);
        btnPrev = view.findViewById(R.id.btn_prev);
        tvQuestion = view.findViewById(R.id.title_question);
        progressIndicator = view.findViewById(R.id.progress_bar);
        progressIndicator.setMax(9);

        progressBar = view.findViewById(R.id.progress_bar_circ);
        progressBar.setVisibility(View.GONE);
        if(this.questionId == 0 )
            btnPrev.setVisibility(View.GONE);

        String text = getResources().getStringArray(R.array.question_text)[questionId];
        tvQuestion.setText(text);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int rId) {
                checkId = rId;
        }});


        btnPrev.setOnClickListener(view1 -> {
            getParentFragmentManager().popBackStackImmediate();
        });
        btnNext.setOnClickListener( view1 ->{
            if(checkId != -1 && questionId != -1) {
                for(int i=0 ; i< CHECK_IDS.length; i++)
                    if (CHECK_IDS[i] == checkId) {
                        viewModel.addOrUpdateQuestionAnswer(questionId, checkId, i + 1);
                        break;
                    }
            }
            if(questionId < 9) {
                Log.d(TAG, "onNextClicked: Next question");
                Bundle bundle = new Bundle();
                bundle.putInt(QUESTION_ID_KEY, questionId + 1);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_question_container, QuestionChildFragment.class,
                                bundle,
                                Integer.toString(questionId))
                        .addToBackStack(null)
                        .commit();
            }else{
                Log.d(TAG, "onViewCreated: Submitted to finish");
                try {
                    viewModel.preCompleteUserRegistration();
                }catch (InvalidParameterException ex){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                listener.onRegistrationStepChanged(RegisterViewModel.STEP_QUIZ,
                        RegisterViewModel.STEP_WAIT);
            }
        });
        subscribeObservers();
    }

    private void subscribeObservers(){
        viewModel.observeQuizModel().removeObservers(getViewLifecycleOwner());
        viewModel.observeQuizModel().observe(getViewLifecycleOwner(),
                new Observer<List<BfpiQuestion>>() {
                    @Override
                    public void onChanged(List<BfpiQuestion> bfpiQuestions) {
                        if(bfpiQuestions != null && questionId < bfpiQuestions.size()){
//                            Log.d(TAG, "onChanged: Updating UI for question " + questionId);
                            BfpiQuestion question = bfpiQuestions.get(questionId);
                            progressIndicator.setProgress(question.getIndex());
                            radioGroup.check(question.getSelectedAnswer());
                            if(question.getIndex() == bfpiQuestions.size() - 1){
                                btnNext.setText(getString(R.string.action_finish));
                                btnNext.setIconResource(R.drawable.ic_baseline_check_24);
                            }
                        }
                    }
                });
    }
}