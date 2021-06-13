package caprita.catalin.cityquest.ui.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.api.auth.LoginRequestDto;
import caprita.catalin.cityquest.ui.api.auth.LoginResponseDto;
import caprita.catalin.cityquest.ui.main.MainActivity;
import caprita.catalin.cityquest.ui.models.AuthResource;
import caprita.catalin.cityquest.ui.register.RegisterActivity;
import caprita.catalin.cityquest.ui.util.Constants;
import dagger.android.support.DaggerAppCompatActivity;

import static caprita.catalin.cityquest.ui.util.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class AuthActivity extends DaggerAppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    private static final String TAG = "AuthActivity";
    public static final String USER_ID_KEY = "userid";
    public static final String USERNAME_KEY = "username";
    public static final String TOKEN_KEY = "token";
    private boolean mLocationPermissionGranted = false;

    private ProgressBar progressBar;
    private ExtendedFloatingActionButton btnSignIn, btnRegister;
    private EditText userNameText, passwordText;
    private AuthViewModel authViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        userNameText = findViewById(R.id.et_username);
        passwordText = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        authViewModel = new ViewModelProvider(this, viewModelProviderFactory)
                .get(AuthViewModel.class);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(userNameText.getText().toString()) || TextUtils.isEmpty(passwordText.getText().toString())){
                    Toast.makeText(AuthActivity.this, "Please fill in your credentials", Toast.LENGTH_LONG).show();
                    return;
                }
                attemptLogin();

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        subscribeObservers();
    }

    private void attemptLogin(){
        LoginRequestDto loginDto = new LoginRequestDto(userNameText.getText().toString(),
                passwordText.getText().toString());
        authViewModel.attemptLogin(loginDto);
    }

    private void subscribeObservers(){
        authViewModel.observeLoginResponse().observe(this, new Observer<AuthResource<LoginResponseDto>>() {
            @Override
            public void onChanged(AuthResource<LoginResponseDto> responseAuthResource) {
                    if(responseAuthResource != null){
                        Log.d(TAG, "onChanged: Response Dto received!");
                        switch (responseAuthResource.status){
                            case LOADING:
                                progressBar.setVisibility(View.VISIBLE);
                                break;
                            case AUTHENTICATED:
                                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                intent.putExtra(USERNAME_KEY, responseAuthResource.data);
                                startActivity(intent);
                                progressBar.setVisibility(View.GONE);
                                finish();
                                break;
                            case ERROR:
                                Toast.makeText(AuthActivity.this, responseAuthResource.message, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                break;
                            case NOT_AUTHENTICATED:
                                progressBar.setVisibility(View.GONE);
                                break;

                        }
                    }
            }
        });
    }


}