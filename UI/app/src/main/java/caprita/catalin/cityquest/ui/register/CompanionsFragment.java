package caprita.catalin.cityquest.ui.register;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.main.companion.CompanionAdapter;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserCompanion;
import caprita.catalin.cityquest.ui.models.UserModel;
import dagger.android.support.DaggerFragment;


public class CompanionsFragment extends DaggerFragment implements AddCompanionDialogFragment.CompanionDialogListener,
CompanionAdapter.OnCompanionClickListener{
    private static final String TAG = "AddCompanionsFragment";
    private static final int SELF_STEP = RegisterViewModel.STEP_ADD_COMPANIONS;

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    RegisterViewModel viewModel;
    private OnRegistrationStepChangedListener listener;

    private ExtendedFloatingActionButton btnNext;
    private FloatingActionButton btnAdd;
    private final List<UserCompanion> companions = new ArrayList<>();
    private RecyclerView rvCompanions;
    public CompanionsFragment() {
        // Required empty public constructor
    }


    public static CompanionsFragment newInstance(String param1, String param2) {
        CompanionsFragment fragment = new CompanionsFragment();
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
        return inflater.inflate(R.layout.fragment_companions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory)
                .get(RegisterViewModel.class);
        btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(view1 ->{
            AddCompanionDialogFragment fragment = new AddCompanionDialogFragment(CompanionsFragment.this, false);
            fragment.show(getParentFragmentManager(), null);
        });

        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener( v ->{
            viewModel.addUserCompanions(this.companions);
        });
        rvCompanions = view.findViewById(R.id.rv_companions);
        rvCompanions.setAdapter(new CompanionAdapter(this.companions, this));
        rvCompanions.setLayoutManager(new LinearLayoutManager(this.getContext(),RecyclerView.VERTICAL,false));
        subscribeObservers();
    }

    private void subscribeObservers(){
        this.viewModel.observeNewUser().removeObservers(getViewLifecycleOwner());
        this.viewModel.observeNewUser().observe(getViewLifecycleOwner(), new Observer<Resource<UserModel>>() {
            @Override
            public void onChanged(Resource<UserModel> resource) {
                if(resource != null && resource.data != null){
                    switch (resource.status) {
                        case SUCCESS: {
                            Log.d(TAG, "onChanged: User model updated successfully");
                            if (SELF_STEP < resource.data.getRegistrationStep()) {
                                listener.onRegistrationStepChanged(SELF_STEP, resource.data.getRegistrationStep());
                            }
                            break;
                        }
                        case ERROR:
                            //Toast.makeText(requireContext(), getString(R.string.err_server_wrong), Toast.LENGTH_SHORT).show();
                            listener.onRegistrationStepFail();
                            break;
                    }
                }
            }
        });

    }

//    DialogListener
    @Override
    public void onCompanionAddConfirm(UserCompanion companion) {
        this.companions.add(companion);
        Log.d(TAG, "onCompanionAddConfirm: Adding new Companion.");
        rvCompanions.getAdapter().notifyItemInserted(this.companions.size() - 1);
    }


    @Override
    public void onRemoveCompanion(int position) {
        if(position < this.companions.size()){
            this.companions.remove(position);
            rvCompanions.getAdapter().notifyItemRemoved(this.companions.size() - 1);
        }
    }

    @Override
    public void onCompanionEdited(UserCompanion companion, int position) {
        if(position < this.companions.size()){
            this.companions.set(position, companion);
            rvCompanions.getAdapter().notifyItemChanged(position);
        }
    }

//  Adapter Listener
    @Override
    public void onClick(int position) {
        AddCompanionDialogFragment fragment = new AddCompanionDialogFragment(CompanionsFragment.this, true);
        fragment.setCompanionPosition(position);
        fragment.show(getParentFragmentManager(), null);
    }
}