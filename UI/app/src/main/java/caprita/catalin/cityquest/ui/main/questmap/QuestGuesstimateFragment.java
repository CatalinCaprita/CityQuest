package caprita.catalin.cityquest.ui.main.questmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.main.OnFullScreenRequestListener;
import caprita.catalin.cityquest.ui.models.enums.QuestType;
import caprita.catalin.cityquest.ui.models.quest.QuestModel;
import dagger.android.support.DaggerFragment;


public class QuestGuesstimateFragment extends DaggerFragment implements SubmitQuestResponseDialogFragment.OnQuestResponseConfirmationListener,
SubtaskListener{

    private static final String TAG = "QuestGuesstimateFragmen";
    public static final String ARG_QUEST_ID = "quest_id";
    private OnFullScreenRequestListener listener;
    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    QuestTypeViewModel viewModel;

    private FragmentContainerView containerView;
    private ProgressBar progressBar;

    private LinearProgressIndicator progressIndicator;
    private ExtendedFloatingActionButton btnConfirm,btnNext, btnPrev, btnBackToMap;
    private MaterialTextView tvCountdown, tvError;
    private Long questId;

    private int totalSubtasks = 0;
    private int currentSubtaskIndex = 0;
    private Long currentPossibleAnswerId = -1L;
    private String currentAnswerValue;
    private CountDownTimer timer;

    private final Bundle bundle = new Bundle();

    public QuestGuesstimateFragment() {
        // Required empty public constructor
    }

    public static QuestGuesstimateFragment newInstance(String param1, String param2) {
        QuestGuesstimateFragment fragment = new QuestGuesstimateFragment();
        return fragment;
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
            this.questId = getArguments().getLong(ARG_QUEST_ID);
            Log.d(TAG, "onCreate: Initialized Quest Fragment for Id" + this.questId.toString());
        }
        listener.onFullScreenRequest();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_guesstimate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelProviderFactory)
                .get(QuestTypeViewModel.class);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        progressIndicator =  view.findViewById(R.id.linear_progress_indicator);
        tvCountdown = view.findViewById(R.id.tv_countdown);
        tvCountdown.setVisibility(View.GONE);
        tvError = view.findViewById(R.id.tv_error);
        tvError.setVisibility(View.GONE);
        containerView = view.findViewById(R.id.fragment_container_view);
        containerView.setVisibility(View.GONE);

        /*Confirmation Button Click means that the user is acknowleding what they should be
        * doing for this quest. It is a preamble for the actual start of the quest.*/
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setVisibility(View.GONE);
        btnConfirm.setOnClickListener( view1 ->{
            btnPrev.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            tvCountdown.setVisibility(View.VISIBLE);
            startTimer();

//          TODO: Modify Here
            bundle.putInt(QuestGuesstimateSubtaskFragment.ARG_SUBTASK_ID, currentSubtaskIndex);
            getChildFragmentManager().beginTransaction()
                    .replace(containerView.getId(), QuestGuesstimateSubtaskFragment.class,bundle,null)
                    .commit();
        });

        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setVisibility(View.GONE);
        btnNext.setOnClickListener(view1 ->{
            if(currentSubtaskIndex < totalSubtasks - 1 ) {
                viewModel.completeSubtaskWithAnswer(currentSubtaskIndex,
                        currentPossibleAnswerId,
                        currentAnswerValue);
                currentSubtaskIndex++;
                progressIndicator.setProgress(currentSubtaskIndex, true);
                if(currentSubtaskIndex == totalSubtasks - 1){
                    btnNext.setText(getString(R.string.action_done));
                    btnNext.setIconResource(R.drawable.ic_baseline_check_24);
                }
//                TODO:REPLACE HERE
                btnNext.setVisibility(View.GONE);
                bundle.putInt(QuestGuesstimateSubtaskFragment.ARG_SUBTASK_ID, currentSubtaskIndex);
                getChildFragmentManager().beginTransaction()
                        .replace(containerView.getId(), QuestGuesstimateSubtaskFragment.class,bundle,null)
//                        .addToBackStack(null)
                        .commit();

            }else if( currentSubtaskIndex == totalSubtasks - 1) {
                viewModel.completeSubtaskWithAnswer(currentSubtaskIndex,
                        currentPossibleAnswerId,
                        currentAnswerValue);
                SubmitQuestResponseDialogFragment dialog = SubmitQuestResponseDialogFragment.newInstance(false);
                this.timer.cancel();
                dialog.setListener(this);
                dialog.show(getChildFragmentManager(), null);
            }
        });

        btnPrev = view.findViewById(R.id.btn_prev);
        btnPrev.setVisibility(View.GONE);
        btnPrev.setOnClickListener(view1 ->{
            currentSubtaskIndex --;
            bundle.putInt(QuestGuesstimateSubtaskFragment.ARG_SUBTASK_ID, currentSubtaskIndex);
            btnNext.setVisibility(View.GONE);
            getChildFragmentManager().beginTransaction()
                    .replace(containerView.getId(), QuestGuesstimateSubtaskFragment.class,bundle,null)
//                        .addToBackStack(null)
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

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: Stopping Timer");
        if(this.timer != null)
            this.timer.cancel();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onConfirm() {
        viewModel.submitUserAnswer();
    }

    @Override
    public void onCancel() {

    }
    private void subscribeObservers() {
        this.viewModel.observeQuest().removeObservers(getViewLifecycleOwner());
        this.viewModel.observeQuest().observe(getViewLifecycleOwner(),
                questModelResource -> {
                    switch (questModelResource.status){
                        case ERROR:
                            onDataError(R.string.err_server_wrong);
                            break;
                        case SUCCESS:
                            if(questModelResource.data != null){
                                onDataSuccess(questModelResource.data);
                            }else{
                                onDataError(R.string.err_quest_404);
                            }
                    }
                });
        this.viewModel.observeQuestResult().removeObservers(getViewLifecycleOwner());
        this.viewModel.observeQuestResult().observe(getViewLifecycleOwner(),
                questResult ->{
                    switch (questResult.status){
                        case ERROR:
                            onDataError(R.string.err_server_wrong);
                            break;
                        case SUCCESS:
                            btnBackToMap.setVisibility(View.VISIBLE);
                            tvCountdown.setVisibility(View.GONE);
                            btnPrev.setVisibility(View.GONE);
                            btnNext.setVisibility(View.GONE);
                            progressIndicator.setVisibility(View.GONE);
//                            TODO: JUMP TO SEE RESULTS
                            Bundle bundle = new Bundle();
                            bundle.putString(QuestResultsFragment.ARG_QUEST_TYPE, QuestType.GUESSTIMATE.toString());
                            getChildFragmentManager()
                                    .beginTransaction()
                                    .replace(containerView.getId(), QuestResultsFragment.class, bundle,null)
                                    .commit();
                            break;
                    }
                });
    }



    private void onDataSuccess(QuestModel model) {
        progressBar.setVisibility(View.GONE);

        btnConfirm.setVisibility(View.VISIBLE);
        containerView.setVisibility(View.VISIBLE);
        Log.d(TAG, "handleSuccess: Data is Aquired for Quest. Initiating Quiz Series.");
        Bundle bundle = new Bundle();
        bundle.putString(QuestPrepareFragment.QUEST_TITLE_KEY, model.getTitle());
        bundle.putString(QuestPrepareFragment.QUEST_TYPE_KEY, model.getType());
        bundle.putInt(QuestPrepareFragment.QUEST_DURATION_KEY, model.getDuration());

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, QuestPrepareFragment.class, bundle)
                .commit();
        initTimer(model.getDuration());
        tvCountdown.setText(getString(R.string.quest_time_left_template,
                Integer.toString(model.getDuration()),
                "00",
                "00"));
        totalSubtasks = model.getSubtasks().size();
        progressIndicator.setMax(totalSubtasks);
        progressIndicator.setProgress(0);
    }


    private void onDataError(int errStringId){
        btnBackToMap.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.GONE);
        tvCountdown.setVisibility(View.GONE);
        containerView.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(errStringId);
    }
    private void initTimer(int quizDuration){
        if(this.timer == null){
            long totalTimeMillis = TimeUnit.MINUTES.toMillis(quizDuration);
            long onceEveryMillis = TimeUnit.SECONDS.toMillis(1);
            this.timer = new CountDownTimer(totalTimeMillis, onceEveryMillis) {
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
                    dialog.setListener(QuestGuesstimateFragment.this);
                    dialog.show(getChildFragmentManager(), null);
                }
            };
        }
    }
    private void startTimer() {
        this.timer.start();
    }


    @Override
    public void onSubtaskCompleted(Long possibleAnswerId) {

    }

    @Override
    public void onSubtaskError() {

    }

    @Override
    public void onSubtaskUpdate(Long possibleAnswerId) {

    }

    @Override
    public void onSubtaskUpdate(Long possibleAnswerId, String userAnswerValue) {
        this.currentPossibleAnswerId = possibleAnswerId;
        this.currentAnswerValue = userAnswerValue;
        btnNext.setVisibility(View.VISIBLE);
    }
}