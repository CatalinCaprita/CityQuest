package caprita.catalin.cityquest.ui.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserModel;
import dagger.android.support.DaggerFragment;


public class WaitFragment extends DaggerFragment {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    RegisterViewModel viewModel;
    ProgressBar progressBar;
    MaterialTextView tvSubheaders, tvWaitStep;
    ExtendedFloatingActionButton btnConfirm;

    private OnRegistrationStepChangedListener listener;
    private final int selfStep = RegisterViewModel.STEP_WAIT;
    private static final String TAG = "WaitFragment";

    public WaitFragment() {
        // Required empty public constructor
    }

    public static WaitFragment newInstance(String param1, String param2) {
        WaitFragment fragment = new WaitFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wait, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar);
        tvSubheaders = view.findViewById(R.id.tv_wait_sh);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        tvWaitStep = view.findViewById(R.id.tv_wait_steps_desc);
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory)
                .get(RegisterViewModel.class);

        btnConfirm.setOnClickListener(view1 -> {
            listener.onRegistrationStepChanged(selfStep, RegisterViewModel.STEP_FINISH);
        });
        subscribeObservers();
    }
    private void subscribeObservers(){
        viewModel.observeNewUser().removeObservers(getViewLifecycleOwner());
        viewModel.observeNewUser().observe(getViewLifecycleOwner(),
                new Observer<Resource<UserModel>>() {
                    @Override
                    public void onChanged(Resource<UserModel> userModelResource) {
                        if(userModelResource != null) {
                            switch (userModelResource.status) {
                                case ERROR:
                                    onError(userModelResource);
                                case SUCCESS:
                                    onSuccess(userModelResource);
                                    break;
                                case LOADING:
                                    progressBar.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    }
                });
    }

    private void onError(Resource<UserModel> resource) {
        progressBar.setVisibility(View.GONE);
        tvWaitStep.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.VISIBLE);
        Log.e(TAG, "onResponse: Received:"+ resource.message );
        tvSubheaders.setText(getString(R.string.err_server_wrong));
        if (resource.data == null) {
            tvSubheaders.setText(getString(R.string.err_500));
            return;
        }
        listener.onRegistrationStepFail();
    }

    private void onSuccess(Resource<UserModel> resource) {
        if (resource.data == null) {
            tvSubheaders.setText(getString(R.string.err_500));
            progressBar.setVisibility(View.GONE);
            tvWaitStep.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.VISIBLE);
            listener.onRegistrationStepFail();
            return;
        }
        progressBar.setVisibility(View.GONE);
            switch (resource.data.getRegistrationStep()) {
                case RegisterViewModel.STEP_PREDICTIONS:
                    tvWaitStep.setText(getString(R.string.hd_wait_desc2));
                    viewModel.computeUserPredictions();
                    break;
                case RegisterViewModel.STEP_FINISH:
                    tvSubheaders.setText(getString(R.string.hd_wait_redirect));
                    tvWaitStep.setVisibility(View.GONE);
                    btnConfirm.setVisibility(View.VISIBLE);
                    break;
                default:
                    tvWaitStep.setText(getString(R.string.hd_wait_desc1));
                    viewModel.completeUserRegistration();
                    break;

            }

    }



}


