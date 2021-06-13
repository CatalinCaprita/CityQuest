package caprita.catalin.cityquest.ui.main.questmap;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.main.OnFullScreenRequestListener;
import caprita.catalin.cityquest.ui.main.userjournal.rv.QuestBriefAdapter;
import caprita.catalin.cityquest.ui.models.maps.QuestTypeMarker;
import caprita.catalin.cityquest.ui.models.quest.QuestBriefModel;
import caprita.catalin.cityquest.ui.models.enums.QuestType;
import caprita.catalin.cityquest.ui.services.LocationService;
import caprita.catalin.cityquest.ui.util.Constants;
import caprita.catalin.cityquest.ui.util.QuestClusterManagerRenderer;
import dagger.android.support.DaggerFragment;

import static android.content.Context.ACTIVITY_SERVICE;


public class QuestMapFragment extends DaggerFragment implements
        BeginQuestDialogFragment.OnStartClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ClusterManager.OnClusterItemClickListener<QuestTypeMarker>,
        ClusterManager.OnClusterItemInfoWindowClickListener<QuestTypeMarker> {

    public static final String QUEST_ID_KEY = "quest_id";

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    QuestMapViewModel viewModel;
    private static final String TAG = "QuestMapFragment";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private final static long UPDATE_INTERVAL = TimeUnit.SECONDS.toMillis(4);
    private final static long FASTEST_INTERVAL = TimeUnit.SECONDS.toMillis(5);
    private final Bundle bundle = new Bundle();
    private boolean isLocationPermissionGranted = false;
    private LocationManager locationManager;

    private MapView mapView;
    private Bundle mapViewBundle;
    private FusedLocationProviderClient locationProviderClient;
    private boolean isTrackingLocation;
    private QuestClusterManagerRenderer renderer;
    private ClusterManager<QuestTypeMarker> clusterManager;
    private GoogleMap googleMap;
    private Location lastKnownLocation;
    private QuestBriefModel clickedQuestBrief;

    private final ActivityResultLauncher<String> requestFineLocationLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean isGranted) {
                            isLocationPermissionGranted = isGranted;
                        }
                    });
    private final ActivityResultLauncher<Intent> requestGpsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                if (!isLocationPermissionGranted)
                                    checkLocationPermission();
                            }
                        }
                    });

    private LocationCallback locationCallback;

    public QuestMapFragment() {
        // Required empty public constructor
    }


    public static QuestMapFragment newInstance() {
        QuestMapFragment fragment = new QuestMapFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        locationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(locationResult != null){
                    if(isTrackingLocation){
//                        Log.d(TAG, "onLocationResult: Location is tracked. Update Map");
//                        updateMapPosition(locationResult.getLastLocation());
                    }
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quest_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelProviderFactory)
                .get(QuestMapViewModel.class);

        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
        subscribeObservers();
        viewModel.queryRemainingQuests();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMapsEnabled()) {
            if (isLocationPermissionGranted) {
                Log.d(TAG, "onResume: locationPermission Granted. Querying Remaining Quests");
                viewModel.queryRemainingQuests();
                startTrackingLocation();
            } else {
                Log.d(TAG, "onResume: locationPermission Not Granted. Requesting Permission");
                checkLocationPermission();
                if (isLocationPermissionGranted) {
                    Log.d(TAG, "onResume: locationPermission Granted. Querying Remaining Quests");
                    startTrackingLocation();
                }
            }
            mapView.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onMapReady(@NonNull @io.reactivex.annotations.NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            launchLocationPermission();
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        //            Initialize the cluster manager
        if(clusterManager == null){
            Log.d(TAG, "addMarkers: Initializing Cluster Manager");
            clusterManager = new ClusterManager<QuestTypeMarker>(requireActivity().getApplicationContext(), googleMap);
        }
        if(renderer == null){
            Log.d(TAG, "addMarkers: Initializing Cluster Renderer");
            renderer = new QuestClusterManagerRenderer(requireActivity(), googleMap, clusterManager);
            clusterManager.setRenderer(renderer);
        }
        GoogleMap.InfoWindowAdapter adapter = new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;
            }
            @Nullable
            @Override
            public View getInfoContents(@NonNull @io.reactivex.annotations.NonNull Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.cv_quest_brief_map, null, false);
                if (clickedQuestBrief != null) {
                    ((MaterialTextView) v.findViewById(R.id.tv_quest_title))
                            .setText(clickedQuestBrief.getTitle());
                    ((MaterialTextView) v.findViewById(R.id.tv_quest_title))
                            .setText(clickedQuestBrief.getTitle());
                    ((MaterialTextView) v.findViewById(R.id.tv_quest_location))
                            .setText(clickedQuestBrief.getLocationName());
                    ((MaterialTextView) v.findViewById(R.id.tv_quest_duration))
                            .setText(getString(R.string.quest_timer_template,
                                    clickedQuestBrief.getDuration()));
                    ((MaterialTextView) v.findViewById(R.id.tv_primary_rwd))
                            .setText(getString(R.string.quest_card_rwd_template,
                                    clickedQuestBrief.getPrimaryRewardAmount(),
                                    clickedQuestBrief.getPrimaryRewardType()
                            ));
                    ((MaterialTextView) v.findViewById(R.id.tv_secondary_rwd))
                            .setText(getString(R.string.quest_card_rwd_template,
                                    clickedQuestBrief.getSecondaryRewardAmount(),
                                    clickedQuestBrief.getSecondaryRewardType()
                            ));
                }
                return v;
            }
        };
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);
        clusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(adapter);
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(adapter);
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        googleMap.setOnMarkerClickListener(clusterManager);
        googleMap.setOnInfoWindowClickListener(clusterManager);

    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        stopTrackingLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onStartClick(Long questId, QuestType questType) {
        Log.d(TAG, "onStartClick: Initiating for quest: " + questId.toString());
        Class<? extends DaggerFragment> questFragment;
        switch (questType) {
            case QUIZ:
                questFragment = QuestQuizFragment.class;
                break;
            case STROLL_AND_SEE:
                questFragment = QuestSnsFragment.class;
                break;
            default:
                questFragment = QuestGuesstimateFragment.class;
        }
        bundle.putLong(QUEST_ID_KEY, questId);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, questFragment, bundle, questFragment.getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull @io.reactivex.annotations.NonNull Location location) {

    }

    @Override
    public boolean onClusterItemClick(QuestTypeMarker questTypeMarker) {
        clickedQuestBrief = questTypeMarker.getQuestBriefModel();
        Log.d(TAG, "onClusterItemClick: Clicked on quest: " + clickedQuestBrief.getTitle());
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(QuestTypeMarker questTypeMarker) {
        BeginQuestDialogFragment dialog = BeginQuestDialogFragment.newInstance(
                questTypeMarker.getQuestBriefModel().getId(),
                questTypeMarker.getQuestBriefModel().getType());
        dialog.setListener(this);
        dialog.show(getChildFragmentManager(), null);
    }
    private void subscribeObservers() {
        viewModel.observeRemainingQuests().removeObservers(getViewLifecycleOwner());
        viewModel.observeRemainingQuests().observe(getViewLifecycleOwner(),
                listResource -> {
                    switch (listResource.status) {
                        case ERROR:
                            if (listResource.code == Constants.Error.CODE_NULL_DATA) {
                                handleEmptyData(R.string.toast_empty_quest_list);
                            } else
                                handleEmptyData(R.string.err_500);
                            break;
                        case SUCCESS:
                            if (listResource.data != null) {
                                List<QuestBriefModel> newData = listResource.data;
                                if (newData.isEmpty()) {
                                    handleEmptyData(R.string.toast_empty_quest_list);
                                } else {
                                    checkLocationPermission();
                                    if(isLocationPermissionGranted)
                                        addMarkers(newData);
                                }
                            }
                    }
                });
    }

    private void handleEmptyData(int stringResId) {
        Log.d(TAG, "handleEmptyData: " + getString(stringResId));
        Toast.makeText(requireContext(), getString(stringResId), Toast.LENGTH_LONG).show();
    }


    private boolean isMapsEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(getString(R.string.require_gps))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.action_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                                @SuppressWarnings("unused") final int id) {
                                Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                requestGpsLauncher.launch(enableGpsIntent);
                            }
                        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //    CHECKING FOR GPS PERMISSIONS
    private void checkLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
        } else {
            launchLocationPermission();
        }
    }

    private void launchLocationPermission() {
        if (shouldShowRequestPermissionRationale(getString(R.string.require_location))) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage(getString(R.string.require_location))
                    .setCancelable(false)
                    .setView(R.layout.fragment_traveler_trait_dialog);
            final AlertDialog alert = builder.create();
            alert.show();
            //        We should always require Location permission
        } else {
            requestFineLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            launchLocationPermission();
            return;
        }
        isTrackingLocation = true;
        locationProviderClient.requestLocationUpdates(createLocationRequest(),
                locationCallback,
                Looper.myLooper());

    }
    private void stopTrackingLocation(){
        if(isTrackingLocation){
            isTrackingLocation = false;
            locationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
    private void updateMapPosition(Location location) {
        float offset = Constants.Quest.MAP_BOUND_OFFSET;
        double bottomBound = location.getLatitude() - offset;
        double leftBound = location.getLongitude() - offset;
        double topBound = location.getLatitude() + offset;
        double rightBound = location.getLongitude() + offset;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                new LatLngBounds(
                        new LatLng(bottomBound, leftBound),
                        new LatLng(topBound, rightBound)
                ), 0));
    }

    private void addMarkers(List<QuestBriefModel> data) {
        if (googleMap != null) {
            Log.d(TAG, "addMarkers: GoogleMap initalized. Adding: " + data.size());
            data.stream().map(QuestTypeMarker::new).forEach(questTypeMarker -> {
                clusterManager.addItem(questTypeMarker);
            });
            clusterManager.cluster();
        }else{
            Log.w(TAG, "GoogleMap not initialized. Will not render any quests.");
        }
    }


    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(getActivity(), LocationService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                requireActivity().startService(serviceIntent);
            }else{
                requireActivity().startService(serviceIntent);
            }
        }
    }

    private LocationRequest createLocationRequest(){
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);
        return mLocationRequestHighAccuracy;
    }
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("caprita.catalin.cityquest.ui.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }


}
