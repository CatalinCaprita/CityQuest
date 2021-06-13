package caprita.catalin.cityquest.ui.register;

import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.auth.AuthActivity;
import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.DaggerFragment;

public class RegisterActivity extends DaggerAppCompatActivity implements OnRegistrationStepChangedListener{

    private static final String TAG = "RegisterActivity";
//    private Toolbar toolbar;

    /*Fragment Container*/
    private FragmentContainerView fragmentContainerView;
    private FragmentManager fragmentManager;
    private static final List<Class<? extends DaggerFragment>> FRAGMENT_ORDER = Arrays.asList(
            CredentialsFragment.class,
            PersonalDataFragment.class,
            CompanionsFragment.class,
            PersonalityQuizFragment.class,
            WaitFragment.class
            );
    private int currentStep = -1;
    private MaterialTextView tvError, tvErrorTitle;
    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        tvError = findViewById(R.id.tv_error);
        tvErrorTitle = findViewById(R.id.tv_title_error);

        fragmentContainerView = findViewById(R.id.fragment_container_view);
        fragmentManager = getSupportFragmentManager();

        if(fragmentManager.getBackStackEntryCount() == 0 ){
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, CredentialsFragment.class,null, CredentialsFragment.class.getSimpleName())
                    .addToBackStack(CredentialsFragment.class.getSimpleName())
                    .commit();
        }
        viewModel = new ViewModelProvider(this, viewModelProviderFactory)
                .get(RegisterViewModel.class);
        currentStep = 0;
    }


    @Override
    public void onRegistrationStepChanged(int fragmentStep, int modelStep) {
        if(modelStep >= FRAGMENT_ORDER.size()){
            Log.d(TAG, "onRegistrationStepComplete: Finished Registration flow.");
            Intent intent = new Intent(RegisterActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }
            Class<? extends DaggerFragment> next = FRAGMENT_ORDER.get(modelStep);
            Log.d(TAG, "onRegistrationStepComplete: Replacing fragment with " + next.getSimpleName());
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, next, null, next.getSimpleName())
//                    .addToBackStack(next.getSimpleName())
                    .commit();

    }

    @Override
    public void onRegistrationStepFail() {
        Log.d(TAG, "onRegistrationStepFail: Rolling Back user registration");
        tvErrorTitle.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_container_view).setVisibility(View.GONE);
//        fragmentManager.popBackStackImmediate();
        viewModel.rollbackUserRegistration();
        ExtendedFloatingActionButton button = findViewById(R.id.btn_back);
        button.setOnClickListener(view1 ->{
            Intent intent = new Intent(RegisterActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        });
        button.setVisibility(View.GONE);
        viewModel.observeRollback().observe(this,
                voidResource -> {
                   button.setVisibility(View.VISIBLE);
                });
    }


}