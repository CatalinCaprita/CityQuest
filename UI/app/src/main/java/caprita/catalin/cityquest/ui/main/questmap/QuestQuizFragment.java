package caprita.catalin.cityquest.ui.main.questmap;

import android.annotation.SuppressLint;
import
        android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.main.OnFullScreenRequestListener;
import caprita.catalin.cityquest.ui.models.enums.QuestType;
import caprita.catalin.cityquest.ui.util.Constants;
import dagger.android.support.DaggerFragment;

public class QuestQuizFragment extends DaggerFragment implements SubtaskListener,
        SubmitQuestResponseDialogFragment.OnQuestResponseConfirmationListener {

    private static final String TAG = "QuestQuizFragment";
    private static final String QUEST_ID_KEY = "quest_id";
    private OnFullScreenRequestListener listener;

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    QuestTypeViewModel viewModel;


    private FragmentContainerView containerView;
    private ProgressBar progressBar;

    private LinearProgressIndicator progressIndicator;
    private ExtendedFloatingActionButton btnConfirm,btnNext, btnPrev, btnBackToMap;
    private MaterialTextView tvCountdown;
    private Long questId;

    private int totalSubtasks;
    private int currentSubtaskIndex = 0;
    private Long currentPossibleAnswerId;
    private CountDownTimer quizTimer;

    private final Bundle bundle = new Bundle();
    public QuestQuizFragment() {
        // Required empty public constructor
    }

    public static QuestQuizFragment newInstance() {
        //        Bundle bundle = new Bundle();
//        bundle.putLong(QUEST_ID_KEY, questId);
//        fragment.setArguments(bundle);
        return new QuestQuizFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFullScreenRequestListener){
            this.listener = (OnFullScreenRequestListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.questId = getArguments().getLong(QUEST_ID_KEY);
            Log.d(TAG, "onCreate: Initialized Quest Fragment for Id" + this.questId.toString());
        }
        listener.onFullScreenRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_quiz, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: Stopping Timer");
        if(this.quizTimer != null)
            this.quizTimer.cancel();
    }
    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, viewModelProviderFactory)
                .get(QuestTypeViewModel.class);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        progressIndicator =  view.findViewById(R.id.linear_progress_indicator);
        tvCountdown = view.findViewById(R.id.tv_countdown);
        tvCountdown.setVisibility(View.GONE);
        containerView = view.findViewById(R.id.fragment_container_view);
        containerView.setVisibility(View.GONE);

        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setVisibility(View.GONE);
        btnConfirm.setOnClickListener( view1 ->{
            btnPrev.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.GONE);
            tvCountdown.setVisibility(View.VISIBLE);
            startTimer();
            bundle.putInt(QuestQuizSubtaskFragment.KEY_SUBTASK_ID, currentSubtaskIndex);
            getChildFragmentManager().beginTransaction()
                    .replace(containerView.getId(), QuestQuizSubtaskFragment.class,bundle,"0")
                    .commit();
        });

        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setVisibility(View.GONE);
        btnNext.setOnClickListener(view1 ->{
            if(currentSubtaskIndex < totalSubtasks - 1 ) {
                viewModel.completeSubtaskWithAnswer(currentSubtaskIndex, currentPossibleAnswerId);
                currentSubtaskIndex++;
                currentPossibleAnswerId = -1L;
                progressIndicator.setProgress(currentSubtaskIndex, true);
                if(currentSubtaskIndex == totalSubtasks -1){
                    btnNext.setText(getString(R.string.action_done));
                    btnNext.setIconResource(R.drawable.ic_baseline_check_24);
                }
                bundle.putInt(QuestQuizSubtaskFragment.KEY_SUBTASK_ID, currentSubtaskIndex);
                getChildFragmentManager().beginTransaction()
                        .replace(containerView.getId(), QuestQuizSubtaskFragment.class, bundle)
                        .commit();

            }else if( currentSubtaskIndex == totalSubtasks - 1) {
                viewModel.completeSubtaskWithAnswer(currentSubtaskIndex, currentPossibleAnswerId);
                SubmitQuestResponseDialogFragment dialog = SubmitQuestResponseDialogFragment.newInstance(false);
                this.quizTimer.cancel();
                dialog.setListener(this);
                dialog.show(getChildFragmentManager(), null);
            }
        });

        btnPrev = view.findViewById(R.id.btn_prev);
        btnPrev.setVisibility(View.GONE);
        btnPrev.setOnClickListener(view1 ->{
            currentSubtaskIndex --;
            currentPossibleAnswerId = -1L;
            bundle.putInt(QuestQuizSubtaskFragment.KEY_SUBTASK_ID, currentSubtaskIndex);
            getChildFragmentManager().beginTransaction()
                    .replace(containerView.getId(), QuestQuizSubtaskFragment.class, bundle)
                    .commit();
        });

        btnBackToMap = view.findViewById(R.id.btn_back_map);
        btnBackToMap.setVisibility(View.GONE);
        btnBackToMap.setOnClickListener( view1 -> {
            listener.onExitFullScreen();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, QuestMapFragment.class, null)
                    .commit();
        });
        subscribeObservers();


        viewModel.queryQuestDetails(this.questId);
    }

    private void initTimer(int quizDuration){
        if(this.quizTimer == null){
            long totalTimeMillis = TimeUnit.MINUTES.toMillis(quizDuration);
            long onceEveryMillis = TimeUnit.SECONDS.toMillis(1);
            this.quizTimer = new CountDownTimer(totalTimeMillis, onceEveryMillis) {
                @SuppressLint("DefaultLocale")
                @Override
                public void onTick(long l) {
                    long remaining = TimeUnit.MILLISECONDS.toSeconds(l) % 60;
                    String ssDisplayFormat;
                    if(remaining < 10)
                        ssDisplayFormat = String.format("0%d", remaining);
                    else
                        ssDisplayFormat = Long.toString(remaining);
                    remaining = TimeUnit.MILLISECONDS.toMinutes(l) % 60;
                    String minDisplayFormat;
                    if(remaining < 10)
                        minDisplayFormat = String.format("0%d", remaining);
                    else
                        minDisplayFormat = Long.toString(remaining);

                    remaining = TimeUnit.MILLISECONDS.toHours(l) % 60;
                    String hDisplayFormat;
                    if(remaining < 10)
                        hDisplayFormat = String.format("0%d", remaining);
                    else
                        hDisplayFormat = Long.toString(remaining);
                    tvCountdown.setText(getString(R.string.quest_time_left_template,
                            hDisplayFormat,
                            minDisplayFormat,
                            ssDisplayFormat
                    ));
                }

                @Override
                public void onFinish() {
                    tvCountdown.setText(getString(R.string.quest_time_up_template));
                    viewModel.completeSubtaskWithAnswer(currentSubtaskIndex, currentPossibleAnswerId);
                    SubmitQuestResponseDialogFragment dialog = SubmitQuestResponseDialogFragment.newInstance(true);
                    dialog.setListener(QuestQuizFragment.this);
                    dialog.show(getChildFragmentManager(), null);
                }
            };
        }
    }
    private void startTimer() {
        this.quizTimer.start();
    }

    private void subscribeObservers() {
        viewModel.observeQuest().removeObservers(getViewLifecycleOwner());
        viewModel.observeQuest().observe(getViewLifecycleOwner(),
                questModelResource -> {
            switch (questModelResource.status){
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), questModelResource.message, Toast.LENGTH_LONG).show();
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);

                    btnConfirm.setVisibility(View.VISIBLE);
                    containerView.setVisibility(View.VISIBLE);
                    Log.d(TAG, "handleSuccess: Data is Aquired for Quest. Initiating Quiz Series.");
                    Bundle bundle = new Bundle();
                    bundle.putString(QuestPrepareFragment.QUEST_TITLE_KEY, questModelResource.data.getTitle());
                    bundle.putString(QuestPrepareFragment.QUEST_TYPE_KEY, questModelResource.data.getType());
                    bundle.putInt(QuestPrepareFragment.QUEST_DURATION_KEY, questModelResource.data.getDuration());

                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container_view, QuestPrepareFragment.class, bundle)
                            .commit();
                    initTimer(questModelResource.data.getDuration());
                    tvCountdown.setText(getString(R.string.quest_time_left_template,
                            Integer.toString(questModelResource.data.getDuration()),
                            "00",
                            "00"));
                    totalSubtasks = questModelResource.data.getSubtasks().size();
                    progressIndicator.setMax(totalSubtasks);
                    progressIndicator.setProgress(0);
                    break;
            }
        });

        viewModel.observeQuestResult().removeObservers(getViewLifecycleOwner());
        viewModel.observeQuestResult().observe(getViewLifecycleOwner(),
                voidResource -> {
                    switch (voidResource.status){
                        case ERROR:
                            Toast.makeText(requireContext(), Constants.Error.SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
                            break;
                        case SUCCESS:
                            btnNext.setVisibility(View.GONE);
                            progressIndicator.setVisibility(View.GONE);
                            btnPrev.setVisibility(View.GONE);
                            btnBackToMap.setVisibility(View.VISIBLE);
                            tvCountdown.setVisibility(View.GONE);
//                            TODO: JUMP TO SEE RESULTS
                            Bundle bundle = new Bundle();
                            bundle.putString(QuestResultsFragment.ARG_QUEST_TYPE, QuestType.QUIZ.toString());
                            getChildFragmentManager()
                                    .beginTransaction()
                                    .replace(containerView.getId(), QuestResultsFragment.class, bundle,null)
                                    .commit();
                            break;

                    }
                });

    }


    @Override
    public void onSubtaskCompleted(Long possibleAnswerId) {

    }

//    Will responsd to a Resource.status == error in the child fragment
    @Override
    public void onSubtaskError() {
        Log.d(TAG, "onSubtaskError: Intercepted error at index" + currentSubtaskIndex);
        getChildFragmentManager().popBackStackImmediate();
        Toast.makeText(requireContext(), Constants.Error.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubtaskUpdate(Long possibleAnswerId) {
        currentPossibleAnswerId = possibleAnswerId;
        Log.d(TAG, "onSubtaskUpdate: SubtaskIndex: " + currentSubtaskIndex + " AnswerPicked: "
                + currentPossibleAnswerId);
    }

    @Override
    public void onSubtaskUpdate(Long possibleAnswerId, String userAnswerValue) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onConfirm() {
        viewModel.submitUserAnswer();
    }

    @Override
    public void onCancel() {

    }
}