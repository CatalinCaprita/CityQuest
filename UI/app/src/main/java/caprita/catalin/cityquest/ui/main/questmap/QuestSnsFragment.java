package caprita.catalin.cityquest.ui.main.questmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.main.OnFullScreenRequestListener;
import caprita.catalin.cityquest.ui.models.enums.QuestType;
import caprita.catalin.cityquest.ui.models.quest.QuestModel;
import dagger.android.support.DaggerFragment;

public class QuestSnsFragment extends DaggerFragment implements SubtaskListener,
        SubmitQuestResponseDialogFragment.OnQuestResponseConfirmationListener {

    private OnFullScreenRequestListener listener;
    private static final String TAG = "QuestSnsFragment";
    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    QuestTypeViewModel viewModel;
    private ExtendedFloatingActionButton btnConfirm, btnBackToMap;
    private MaterialTextView tvCountdown, tvError;
    private FragmentContainerView containerView;
    private CountDownTimer timer;
    private Long questId;
    private QuestType questType;
    public QuestSnsFragment() {
    }

    public static QuestSnsFragment newInstance(String param1, String param2) {
        QuestSnsFragment fragment = new QuestSnsFragment();
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
            this.questId = getArguments().getLong(QuestMapFragment.QUEST_ID_KEY);
        }
        listener.onFullScreenRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_sns, container, false);
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
                    SubmitQuestResponseDialogFragment dialog = SubmitQuestResponseDialogFragment.newInstance(true);
                    dialog.setListener(QuestSnsFragment.this);
                    dialog.show(getChildFragmentManager(), null);
                }
            };
        }
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelProviderFactory)
                .get(QuestTypeViewModel.class);

        tvCountdown = view.findViewById(R.id.tv_countdown);
        tvCountdown.setVisibility(View.GONE);
        containerView = view.findViewById(R.id.fragment_container_view);
        containerView.setVisibility(View.GONE);

        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setVisibility(View.GONE);
        btnConfirm.setOnClickListener( view1 ->{
            if(btnConfirm.getText().toString().equals(getString(R.string.action_done))){
                this.timer.cancel();
                SubmitQuestResponseDialogFragment dialog = SubmitQuestResponseDialogFragment.newInstance(false);
                dialog.setListener(QuestSnsFragment.this);
                dialog.show(getChildFragmentManager(), null);
            }else {
                btnConfirm.setText(R.string.action_done);
                btnConfirm.setIconResource(R.drawable.ic_baseline_check_24);
                tvCountdown.setVisibility(View.VISIBLE);
                startTimer();
                getChildFragmentManager().beginTransaction()
                        .replace(containerView.getId(), QuestSnsSubtasksFragment.class, null, null)
                        .commit();
            }
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
        tvError = view.findViewById(R.id.tv_error);
        tvError.setVisibility(View.GONE);
        subscribeObservers();
        viewModel.queryQuestDetails(questId);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: Stopping Timer");
        if(this.timer != null)
            this.timer.cancel();
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
//                            TODO: JUMP TO SEE RESULTS
                    Bundle bundle = new Bundle();
                    bundle.putString(QuestResultsFragment.ARG_QUEST_TYPE, QuestType.STROLL_AND_SEE.toString());
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(containerView.getId(), QuestResultsFragment.class, bundle,null)
                            .commit();
                    break;
            }
                });

    }

    @SuppressLint("DefaultLocale")
    private void onDataSuccess(QuestModel model){
        btnConfirm.setVisibility(View.VISIBLE);
        tvCountdown.setVisibility(View.VISIBLE);
        containerView.setVisibility(View.VISIBLE);
        initTimer(model.getDuration());

        this.questType = QuestType.valueOf(model.getType());
        Bundle bundle = new Bundle();
        bundle.putString(QuestPrepareFragment.QUEST_TITLE_KEY, model.getTitle());
        bundle.putString(QuestPrepareFragment.QUEST_TYPE_KEY, model.getType());
        bundle.putInt(QuestPrepareFragment.QUEST_DURATION_KEY, model.getDuration());

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, QuestPrepareFragment.class, bundle)
                .commit();
        tvCountdown.setText(getString(R.string.quest_time_left_template,
                String.format("0%d",model.getDuration())
                , "00", "00"));

    }
    private void onDataError(int errStringId){
        btnBackToMap.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.GONE);
        tvCountdown.setVisibility(View.GONE);
        containerView.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(errStringId);
    }
    private void startTimer() {
        this.timer.start();
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onConfirm() {
        viewModel.submitUserAnswer();
    }

    @Override
    public void onCancel() {

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

    }
}