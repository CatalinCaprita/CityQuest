package caprita.catalin.cityquest.ui.main.profile;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.Resource;
import caprita.catalin.cityquest.ui.models.UserModel;
import caprita.catalin.cityquest.ui.util.Constants;
import dagger.android.support.DaggerFragment;

import static android.view.View.GONE;


public class StatsFragment extends DaggerFragment {

    private static final String TAG = "StatsFragment";
    private ProgressBar pbKnowledge, pbVitality, pbSwiftness, pbSociability, pbQuests;
    private CircularProgressIndicator progressBar;
    private ShapeableImageView ivKnowledge, ivVitality, ivSwiftness, ivSociability;
    private MaterialTextView tvKnowledge, tvVitality, tvSwiftness, tvSociability, tvQuests;
    private ShapeableImageView ivPersonality;
    private RadarChart chartPersonality;

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    ProfileViewModel viewModel;
    private final Map<Integer, Integer> traitToDescRes = new HashMap<>();
    private final List<RadarEntry> entries = new ArrayList<>();
    public StatsFragment() {

        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getParentFragment() != null;
        viewModel = new ViewModelProvider(getParentFragment(), viewModelProviderFactory)
                .get(ProfileViewModel.class);
        pbQuests = view.findViewById(R.id.pb_quests);
        pbKnowledge = view.findViewById(R.id.pb_knowledge);
        pbVitality = view.findViewById(R.id.pb_vitality);
        pbSwiftness = view.findViewById(R.id.pb_swiftness);
        pbSociability = view.findViewById(R.id.pb_sociablity);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(GONE);

        ivKnowledge = view.findViewById(R.id.iv_knowledge);
        ivKnowledge.setOnClickListener(v -> {this.spawnAlertDescription(R.string.label_progres_knowledge);});
        ivVitality = view.findViewById(R.id.iv_vitality);
        ivVitality.setOnClickListener(v -> {this.spawnAlertDescription(R.string.label_progres_vitality);});
        ivSwiftness = view.findViewById(R.id.iv_swiftness);
        ivSwiftness.setOnClickListener(v -> {this.spawnAlertDescription(R.string.label_progres_swiftness);});
        ivSociability = view.findViewById(R.id.iv_sociability);
        ivSociability.setOnClickListener(v -> {this.spawnAlertDescription(R.string.label_progres_sociability);});

        tvQuests = view.findViewById(R.id.tv_quest_desc);
        traitToDescRes.put(R.string.label_progres_knowledge, R.string.profile_knowledge_description);
        traitToDescRes.put(R.string.label_progres_swiftness, R.string.profile_swiftness_description);
        traitToDescRes.put(R.string.label_progres_vitality, R.string.profile_vitality_description);
        traitToDescRes.put(R.string.label_progres_sociability, R.string.profile_sociability_description);

        chartPersonality = view.findViewById(R.id.chart_personality);
        chartPersonality.setDrawWeb(true);

        XAxis xAxis = chartPersonality.getXAxis();
        xAxis.setTextSize(14f);
        xAxis.setTextColor(R.color.col_text_primary);
        xAxis.setValueFormatter(
                new IndexAxisValueFormatter(new String[]{"Openness", "Conscienciousness",
                        "Extrovertism","Agreeableness","Neuroticism"
                }));

        initChartData();
        YAxis yAxis = chartPersonality.getYAxis();
        yAxis.setTypeface(Typeface.SANS_SERIF);
        yAxis.setTextSize(9f);

        yAxis.setAxisMinimum(1f);
        yAxis.setAxisMaximum(5f);
        yAxis.setTextColor(R.color.col_text_primary);
        subscribeObservers();

    }

    private void subscribeObservers() {
        viewModel.observeUserModel().removeObservers(getViewLifecycleOwner());
        viewModel.observeUserModel().observe(getViewLifecycleOwner(),
                new Observer<Resource<UserModel>>() {
                    @Override
                    public void onChanged(Resource<UserModel> resource) {
                        if (resource != null) {
                            Log.d(TAG, "onChanged: Setting up new user companions resource");
                            switch (resource.status) {
                                case ERROR:
                                    handleError(resource.message);
                                case SUCCESS:
                                    if (resource.data != null)
                                        handleSucces(resource.data);
                                    else
                                        handleError(Constants.Error.SOMETHING_WENT_WRONG);
                                    break;
                                case LOADING:
                                    progressBar.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    }
                });

    }

    private void handleSucces(UserModel data) {
        pbKnowledge.setProgress(data.getKnowledge());
        pbVitality.setProgress(data.getVitality());
        pbSwiftness.setProgress(data.getSwiftness());
        pbSociability.setProgress(data.getSociability());
        tvQuests.setText(getString(R.string.profile_quests_progress, data.getTotalFinishedQuests()));
        pbQuests.setProgress(data.getTotalFinishedQuests());
        updateChartData(data);

    }

    private void initChartData() {
        for(int i=0 ; i < 5; i++){
            entries.add(new RadarEntry(1f));
        }
        RadarDataSet set1 = new RadarDataSet(entries, "Personality Traits");
        int color = ContextCompat.getColor(requireContext(), R.color.color_accent);
        set1.setColor(color);
        set1.setFillColor(color);
        set1.setDrawFilled(true);
        set1.setLineWidth(2f);
        set1.setHighlightCircleInnerRadius(3f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setHighlightCircleOuterRadius(20f);
        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        RadarData data = new RadarData(sets);
        data.setValueTextSize(20f);

        data.setDrawValues(true);
        chartPersonality.setData(data);
        chartPersonality.invalidate();
    }

    private void updateChartData(UserModel data) {
        entries.get(0).setY((float)data.getOteScore());
        entries.get(1).setY((float)data.getConScore());
        entries.get(2).setY((float)data.getExtScore());
        entries.get(3).setY((float)data.getAgrScore());
        entries.get(4).setY((float)data.getNeurScore());
        chartPersonality.notifyDataSetChanged();
        chartPersonality.invalidate();

    }

    private void handleError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void spawnAlertDescription(Integer traitKey){
        String desc = getString(traitToDescRes.get(traitKey));
        String title = getString(traitKey);
        TravelerTraitDialogFragment fragment = TravelerTraitDialogFragment.newInstance(title,desc);
        fragment.show(getChildFragmentManager(),null);
    }
}