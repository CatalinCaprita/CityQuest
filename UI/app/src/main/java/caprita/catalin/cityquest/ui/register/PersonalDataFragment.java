package caprita.catalin.cityquest.ui.register;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.api.register.UserPersonalDataDto;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserModel;
import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalDataFragment extends DaggerFragment {

    private static final String TAG = "PersonalDataFragment";
    TextInputLayout tvFirstname, tvLastname, tvGender, tvTravelType;
    MaterialButton btnConfirm;
    ProgressBar progressBar;
    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    RegisterViewModel viewModel;
    private static final int SELF_STEP = RegisterViewModel.STEP_PERS_DATA;
    private OnRegistrationStepChangedListener listener;

    public PersonalDataFragment() {
        // Required empty public constructor
    }


    public static PersonalDataFragment newInstance(String param1, String param2) {
        PersonalDataFragment fragment = new PersonalDataFragment();
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
        return inflater.inflate(R.layout.fragment_personal_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Inside.");
        tvFirstname = view.findViewById(R.id.tv_firstname);
        tvLastname = view.findViewById(R.id.tv_lastname);
        tvGender = view.findViewById(R.id.tv_gender);
        tvTravelType = view.findViewById(R.id.tv_travel_type);
        List<String> list = Arrays.asList("Male","Female");
        ((AutoCompleteTextView)tvGender.getEditText()).setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, list));

        List<String> travelType = Arrays.asList("Alone","Family","Friends and Family", "Friends");
        ((AutoCompleteTextView)tvTravelType.getEditText()).setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, travelType));
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory)
                .get(RegisterViewModel.class);

        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                if(TextUtils.isEmpty(tvFirstname.getEditText().getText())) {
                    tvFirstname.setError(getString(R.string.err_empty));
                    valid = false;
                }
                if(TextUtils.isEmpty(tvLastname.getEditText().getText())){
                    tvLastname.setError(getString(R.string.err_empty));
                    valid = false;
                }
                if(TextUtils.isEmpty(tvGender.getEditText().getText())){
                    tvGender.setError(getString(R.string.err_empty));
                    valid = false;
                }
                if(!valid){
                    return;
                }
//                ByPass Name and Gender verification, go directly to the Quiz section
                updateUserDetails();
            }
        });
        subscribeObservers();
    }

    private void updateUserDetails() {
        UserPersonalDataDto dto = new UserPersonalDataDto();
        dto.setFirstName(tvFirstname.getEditText().getText().toString());
        dto.setLastName(tvLastname.getEditText().getText().toString());
        dto.setGender(tvGender.getEditText().getText().toString());
        if(TextUtils.equals(tvTravelType.getEditText().getText(), "Alone"))
            dto.setAlone(true);
        viewModel.addNewUserDetails(dto);
    }

    private void subscribeObservers(){
        Log.d(TAG, "subscribeObservers: Setting up subscribers");
        viewModel.observeNewUser().removeObservers(getViewLifecycleOwner());
        viewModel.observeNewUser().observe(getViewLifecycleOwner(),
                new Observer<Resource<UserModel>>() {
                    @Override
                    public void onChanged(Resource<UserModel> userModelResource) {
                        if(userModelResource != null){
                            switch (userModelResource.status){
                                case LOADING:
                                    progressBar.setVisibility(View.VISIBLE);
                                    break;
                                case SUCCESS:
                                    if(userModelResource.data != null)
                                        onModelSuccess(userModelResource.data);
                                    break;
                                case ERROR:
                                   Toast.makeText(requireContext(), userModelResource.message, Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }
                });
    }

    private void onModelSuccess(UserModel data) {
        tvFirstname.getEditText().setText(data.getFirstName());
        tvLastname.getEditText().setText(data.getLastName());
        if(SELF_STEP < data.getRegistrationStep()){
            Log.d(TAG, "onModelSuccess: Alerting activity for step: " + data.getRegistrationStep());
            listener.onRegistrationStepChanged(SELF_STEP, data.getRegistrationStep());
        }
    }
}