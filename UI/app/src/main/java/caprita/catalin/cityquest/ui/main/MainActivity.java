package caprita.catalin.cityquest.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.api.auth.LoginResponseDto;
import caprita.catalin.cityquest.ui.auth.AuthActivity;
import caprita.catalin.cityquest.ui.dagger.viewmodel.ViewModelProviderFactory;
import caprita.catalin.cityquest.ui.main.profile.ProfileFragment;
import caprita.catalin.cityquest.ui.main.questmap.QuestMapFragment;
import caprita.catalin.cityquest.ui.main.userjournal.UserJournalFragment;
import caprita.catalin.cityquest.ui.models.AuthResource;
import caprita.catalin.cityquest.ui.api.auth.SessionManager;
import caprita.catalin.cityquest.ui.services.LocationService;
import caprita.catalin.cityquest.ui.util.Constants;
import dagger.android.support.DaggerAppCompatActivity;

import static caprita.catalin.cityquest.ui.util.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static caprita.catalin.cityquest.ui.util.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

/**
 * The main activity of our single activity archtecture, consisting of a single activity that displays
 * the bottom navigation menu. The fragments will be restorted or retrieved from this activitiy in the
 * corresponding container*/
public class MainActivity extends DaggerAppCompatActivity implements OnFullScreenRequestListener{

    /*Bottom Navigation*/
    private BottomNavigationView bottomNavigationView;
    /*Fragment Container*/
    private FragmentContainerView fragmentContainerView;
    private FragmentManager fragmentManager;
    private boolean locationPermissionGranted = false;

    @Inject
    ViewModelProviderFactory viewModelProviderFactory;

    @Inject
    SessionManager sessionManager;

    public static final int RC_LOGIN = 1;
    private static final String TAG = MainActivity.class.getSimpleName();



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainerView = findViewById(R.id.fragment_container_view);
        fragmentManager = getSupportFragmentManager();

        /*Set bottom Navigation */
        bottomNavigationView = findViewById(R.id.nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Class<? extends Fragment> targetFragment;
                Class<? extends Fragment> currentFragment = fragmentManager.findFragmentById(R.id.fragment_container_view).getClass();
                /*Defaults to the UserJournalFragment*/
                switch (item.getItemId()){
                    case R.id.nav_item_stats:
                        targetFragment = ProfileFragment.class;
                        break;
                    case R.id.nav_item_quests:
                        targetFragment = QuestMapFragment.class;
                        break;
                    default:
                        targetFragment = UserJournalFragment.class;
                        break;
                };
                if(targetFragment != currentFragment){
                    Log.d(TAG, "onNavigationItemSelected: Replacing current fragment.");
                    Fragment lookup = fragmentManager.findFragmentByTag(targetFragment.getSimpleName());
                    if(lookup == null) {
                        Log.d(TAG, "onNavigationItemSelected: Adding new fragment to backstack");
                        fragmentManager.beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragment_container_view, targetFragment, null, targetFragment.getSimpleName())
                                .addToBackStack(null)
                                .commit();
                    }else{
                        Log.d(TAG, "onNavigationItemSelected: Placing existing fragment to backstack");
                        fragmentManager.beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.fragment_container_view, lookup)
                                .commit();
                    }
                }else{
                    Log.d(TAG, "onNavigationItemSelected: Clicked on the same button,ergo doing nothing");
                }
                return true;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isServicesOk()){
            observeUserAuthState();
            fragmentContainerView.setVisibility(View.VISIBLE);
        }else{
            fragmentContainerView.setVisibility(View.GONE);
        }
    }

    private void observeUserAuthState(){
        sessionManager.observeLoginResponse().observe(this, new Observer<AuthResource<LoginResponseDto>>() {
            @Override
            public void onChanged(AuthResource<LoginResponseDto> responseAuthResource) {
                if(responseAuthResource != null){
                    switch (responseAuthResource.status){
                        case ERROR:
                        case NOT_AUTHENTICATED:
                            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case AUTHENTICATED:
                            if(fragmentManager.getBackStackEntryCount() == 0)
                                fragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container_view,
                                            UserJournalFragment.class,null,UserJournalFragment.class.getSimpleName())
                                    .commit();
                            break;

                    }
                }
            }
        });
    }

    @Override
    public void onFullScreenRequest() {
        this.bottomNavigationView.setVisibility(View.GONE);
        this.fragmentContainerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onExitFullScreen() {
        this.bottomNavigationView.setVisibility(View.VISIBLE);
        this.fragmentContainerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

    }

//
//    private boolean checkMapServices(){
//        if(isServicesOk()){
//            return isMapsEnabled();
//        }
//        return false;
//    }

    private boolean isServicesOk(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: Fixable Error encountered.");
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(MainActivity.this, available, Constants.ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, getString(R.string.enable_maps_request), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

//    private boolean isMapsEnabled(){
//        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
//        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
//            buildAlertMessageNoGps();
//            return false;
//        }
//        return true;
//    }
//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(getString(R.string.require_gps))
//                .setCancelable(false)
//                .setPositiveButton(getString(R.string.action_confirm_dialog),
//                        new DialogInterface.OnClickListener() {
//                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
//                                        @SuppressWarnings("unused") final int id) {
//                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    private void getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            locationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String permissions[],
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        locationPermissionGranted = false;
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    locationPermissionGranted = true;
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: called.");
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ENABLE_GPS: {
//                if(locationPermissionGranted){
//                    observeUserAuthState();
//                }
//                else{
//                    getLocationPermission();
//                }
//            }
//        }

//    }
}