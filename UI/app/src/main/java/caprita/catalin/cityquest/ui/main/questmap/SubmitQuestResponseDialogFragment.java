package caprita.catalin.cityquest.ui.main.questmap;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import caprita.catalin.cityquest.ui.R;


public class SubmitQuestResponseDialogFragment extends DialogFragment {

    private boolean isTimeUp;
    private static final String ARG_TIME_UP = "time_up";
    private OnQuestResponseConfirmationListener listener;


    public interface OnQuestResponseConfirmationListener{
        void onConfirm();
        void onCancel();
    }
    public SubmitQuestResponseDialogFragment() {
        // Required empty public constructor
    }

    public static SubmitQuestResponseDialogFragment newInstance(boolean isTimeUp) {
        SubmitQuestResponseDialogFragment fragment = new SubmitQuestResponseDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_TIME_UP, isTimeUp);
        fragment.setArguments(args);
        return fragment;
    }

    public OnQuestResponseConfirmationListener getListener() {
        return listener;
    }

    public void setListener(OnQuestResponseConfirmationListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull @io.reactivex.annotations.NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnQuestResponseConfirmationListener)
            this.listener = (OnQuestResponseConfirmationListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isTimeUp = getArguments().getBoolean(ARG_TIME_UP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submit_quest_response_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.annotations.NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialTextView tvUserConfirmation = view.findViewById(R.id.tv_user_confirmation);
        if(isTimeUp)
            tvUserConfirmation.setText(getString(R.string.time_is_up));
        else
            tvUserConfirmation.setText(getString(R.string.are_you_sure));

        MaterialButton btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(view1 ->{
            this.listener.onConfirm();
            dismiss();
        });
        MaterialButton btnTimeUpConfirm = view.findViewById(R.id.btn_time_up_confirm);
        btnTimeUpConfirm.setOnClickListener(view1 ->{
            this.listener.onConfirm();
            dismiss();
        });
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        if(isTimeUp) {
            btnCancel.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            btnTimeUpConfirm.setVisibility(View.VISIBLE);
        }else {
            btnTimeUpConfirm.setVisibility(View.GONE);

            btnCancel.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(v -> {
                this.listener.onCancel();
                this.dismiss();
            });
        }
    }


}