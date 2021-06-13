package caprita.catalin.cityquest.ui.main.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.api.user.UpdateUserDto;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserModel;
import dagger.android.support.DaggerFragment;

public class UserInfoFragment extends DaggerFragment {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    ProfileViewModel viewModel;
    private ProgressBar progressBar;
    private RelativeLayout containter;
    private TextInputLayout tvFirstname, tvLastname, tvEmail, tvGender, tvJoinDate;
    private boolean inEditMode = false;
    private MaterialButton btnSubmitChanges;
    public static final String KEY_EDIT_MODE = "edit_mode";
    public UserInfoFragment() {
        // Required empty public constructor
    }

    public static UserInfoFragment newInstance(String param1, String param2) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            inEditMode = getArguments().getBoolean(KEY_EDIT_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getParentFragment() != null;
        viewModel = new ViewModelProvider(getParentFragment(), viewModelProviderFactory)
                .get(ProfileViewModel.class);
        tvFirstname = view.findViewById(R.id.tv_firstname);
        tvFirstname.setEnabled(inEditMode);
        tvLastname = view.findViewById(R.id.tv_lastname);
        tvLastname.setEnabled(inEditMode);
        tvGender = view.findViewById(R.id.tv_gender);
        tvGender.setEnabled(false);
        tvEmail = view.findViewById(R.id.tv_email);
        tvEmail.setEnabled(inEditMode);
        containter = view.findViewById(R.id.container);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        tvJoinDate = view.findViewById(R.id.tv_joindate);
        tvJoinDate.setEnabled(false);

        btnSubmitChanges = view.findViewById(R.id.btn_confirm);
        btnSubmitChanges.setOnClickListener(this::updateUserInfo);
        btnSubmitChanges.setVisibility(inEditMode ? View.VISIBLE : View.GONE);
        subscribeObservers();
    }

    private void subscribeObservers() {
        viewModel.observeUserModel().removeObservers(getViewLifecycleOwner());
        viewModel.observeUserModel().observe(getViewLifecycleOwner(), new Observer<Resource<UserModel>>() {
            @Override
            public void onChanged(Resource<UserModel> resource) {
                if(resource != null){
                    switch (resource.status){
                        case LOADING:
                            progressBar.setVisibility(View.VISIBLE);
                            containter.setVisibility(View.GONE);
                            break;
                        case ERROR:
                            containter.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                            break;
                        case SUCCESS:
                            if(resource.data != null){
                                onResourceSuccess(resource.data);
                            }
                            break;
                    }
                }
            }
        });
    }

    private void onResourceSuccess(UserModel data) {
        progressBar.setVisibility(View.GONE);
        containter.setVisibility(View.VISIBLE);

        Objects.requireNonNull(tvFirstname.getEditText()).setText(data.getFirstName());
        Objects.requireNonNull(tvLastname.getEditText()).setText(data.getLastName());
        Objects.requireNonNull(tvGender.getEditText()).setText(data.getGender());
        Objects.requireNonNull(tvEmail.getEditText()).setText(data.getEmail());
        Objects.requireNonNull(tvJoinDate.getEditText()).setText(data.getJoinDate());

    }
    private void updateUserInfo(View v){
        boolean valid = true;
        if(TextUtils.isEmpty(tvFirstname.getEditText().getText())) {
            tvFirstname.setErrorEnabled(true);
            tvFirstname.setError(getString(R.string.err_empty));
            valid = false;
        }
        if(TextUtils.isEmpty(tvLastname.getEditText().getText())){
            tvLastname.setErrorEnabled(true);
            tvLastname.setError(getString(R.string.err_empty));
            valid = false;
        }
        if(TextUtils.isEmpty(tvEmail.getEditText().getText())){
            tvEmail.setErrorEnabled(true);
            tvGender.setError(getString(R.string.err_empty));
            valid = false;
        }
        if(!valid){
            return;
        }
        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail(tvEmail.getEditText().getText().toString());
        dto.setFirstName(tvFirstname.getEditText().getText().toString());
        dto.setLastName(tvLastname.getEditText().getText().toString());
        viewModel.updateUser(dto);

    }

}