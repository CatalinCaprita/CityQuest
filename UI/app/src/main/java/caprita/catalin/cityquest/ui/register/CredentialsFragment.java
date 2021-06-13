package caprita.catalin.cityquest.ui.register;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.api.register.RegisterDto;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserModel;
import dagger.android.support.DaggerFragment;


public class CredentialsFragment extends DaggerFragment {

    TextInputLayout tvUsername, tvPassword, tvConfirm, tvEmail;
    MaterialButton btnConfirm;
    private static final String TAG = "CredentialsFragment";
    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    RegisterViewModel viewModel;
    ProgressBar progressBar;
    private OnRegistrationStepChangedListener listener;
    private final int selfStep = RegisterViewModel.STEP_CREATE;
    public CredentialsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CredentialsFragment newInstance(String param1, String param2) {
        CredentialsFragment fragment = new CredentialsFragment();
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
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credentials, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername = view.findViewById(R.id.tv_username);
        tvPassword = view.findViewById(R.id.tv_password);
        tvConfirm = view.findViewById(R.id.tv_password_confirm);
        tvEmail = view.findViewById(R.id.tv_email);
        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory)
                .get(RegisterViewModel.class);

        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Check if everything is filled
                boolean valid = true;
                if(TextUtils.isEmpty(tvUsername.getEditText().getText())) {
                    tvUsername.setError(getString(R.string.err_empty));
                    valid = false;
                }
                if(TextUtils.isEmpty(tvPassword.getEditText().getText())){
                    tvPassword.setError(getString(R.string.err_empty));
                    valid = false;
                }
                if(TextUtils.isEmpty(tvConfirm.getEditText().getText())){
                    tvConfirm.setError(getString(R.string.err_empty));
                    valid = false;
                }
                if(TextUtils.isEmpty(tvEmail.getEditText().getText())) {
                    tvEmail.setError(getString(R.string.err_empty));
                    valid = false;
                }
                if(!valid)
                    return;

                if(!TextUtils.equals(tvPassword.getEditText().getText(), tvConfirm.getEditText().getText())){
                    tvPassword.setError(getString(R.string.err_passwords_not_matching));
                    tvConfirm.setError(getString(R.string.err_passwords_not_matching));
                    return;
                }
                attemptNewUserRegistration();

            }
        });
        subscribeObservers();

    }

    private void attemptNewUserRegistration() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername(tvUsername.getEditText().getText().toString());
        dto.setPassword(tvConfirm.getEditText().getText().toString());
        dto.setEmail(tvEmail.getEditText().getText().toString());
        viewModel.attemptRegisterUser(dto);
    }

    private void subscribeObservers(){
        viewModel.observeNewUser().removeObservers(getViewLifecycleOwner());
        viewModel.observeNewUser().observe(getViewLifecycleOwner(),
                new Observer<Resource<UserModel>>() {
            @Override
            public void onChanged(Resource<UserModel> userModelResource) {
                if(userModelResource != null){
                    Log.d(TAG, "onChanged: Reacting to user");
                    switch (userModelResource.status){
                        case LOADING:
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            Log.d(TAG, "onChanged: Alerting activity.");
                            if(userModelResource.data != null)
                                onModelSuccess(userModelResource.data);
                            break;
                        case ERROR:
                            if(userModelResource.code != null){
                                if (userModelResource.code == 500) {
                                    listener.onRegistrationStepFail();
                                } else {
                                    Toast.makeText(requireContext(), userModelResource.message, Toast.LENGTH_LONG).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            break;
                    }
                }
            }
        });
    }

    private void onModelSuccess(UserModel data) {
        progressBar.setVisibility(View.GONE);
        tvUsername.getEditText().setText(data.getUsername());
        tvEmail.getEditText().setText(data.getEmail());
        if(selfStep < data.getRegistrationStep()){
            Log.d(TAG, "onModelSuccess: Alerting activity for step: " + data.getRegistrationStep());
            listener.onRegistrationStepChanged(selfStep, data.getRegistrationStep());
        }
    }

}