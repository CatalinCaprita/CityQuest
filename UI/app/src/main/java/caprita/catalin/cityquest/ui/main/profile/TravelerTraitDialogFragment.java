package caprita.catalin.cityquest.ui.main.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textview.MaterialTextView;

import caprita.catalin.cityquest.ui.R;


public class TravelerTraitDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TRAIT_DESC = "trait_desc";
    private static final String TRAIT_NAME = "trait_name";
    // TODO: Rename and change types of parameters
    private String traitDesc;
    private String traitName;

    private MaterialTextView tvTitle, tvDesc;

    public TravelerTraitDialogFragment() {
        // Required empty public constructor
    }

    public static TravelerTraitDialogFragment newInstance(String traitName, String traitDesc) {
        TravelerTraitDialogFragment fragment = new TravelerTraitDialogFragment();
        Bundle args = new Bundle();
        args.putString(TRAIT_DESC, traitDesc);
        args.putString(TRAIT_NAME, traitName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            traitDesc = getArguments().getString(TRAIT_DESC);
            traitName = getArguments().getString(TRAIT_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_traveler_trait_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(traitName);
        tvDesc = view.findViewById(R.id.tv_description);
        tvDesc.setText(traitDesc);
    }

}