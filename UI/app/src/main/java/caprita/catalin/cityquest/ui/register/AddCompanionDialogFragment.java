package caprita.catalin.cityquest.ui.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.UserCompanion;

public class AddCompanionDialogFragment extends DialogFragment {
    private CompanionDialogListener listener;
    private TextInputLayout tvName, tvNickName;
    private MaterialButton btnConfirm,btnRemove;
    private boolean isRemovable = false;
    private int companionPosition;

    public AddCompanionDialogFragment(CompanionDialogListener listener, boolean removable) {
        // Required empty public constructor
        this.listener = listener;
        this.isRemovable = removable;
        if(!this.isRemovable)
            companionPosition = -1;
    }

    public int getCompanionPosition() {
        return companionPosition;
    }

    public void setCompanionPosition(int companionPosition) {
        this.companionPosition = companionPosition;
    }

    public AddCompanionDialogFragment() {
    }

    public static AddCompanionDialogFragment newInstance() {
        AddCompanionDialogFragment fragment = new AddCompanionDialogFragment();
        return fragment;
    }

    public CompanionDialogListener getListener() {
        return listener;
    }

    public void setListener(CompanionDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_companion_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.tv_name);
        tvNickName = view.findViewById(R.id.tv_nickname);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener( view1 ->{
            UserCompanion companion = new UserCompanion();
            if(TextUtils.isEmpty(tvName.getEditText().getText())) {
                tvName.setError(getString(R.string.err_empty));
                return;
            }
            tvName.setError(null);
            companion.setName(tvName.getEditText().getText().toString());
            companion.setNickname(tvNickName.getEditText().getText().toString());
            companion.setImageResource(R.drawable.ic_baseline_person_24);
            if(this.companionPosition == -1)
                listener.onCompanionAddConfirm(companion);
            else
                listener.onCompanionEdited(companion, this.companionPosition);
            this.dismiss();
        });

        btnRemove = view.findViewById(R.id.btn_remove);
        if(this.isRemovable) {
            btnRemove.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btnConfirm.getLayoutParams();
            params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            btnConfirm.setLayoutParams(params);
        }
        else {
            btnRemove.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btnConfirm.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            btnConfirm.setLayoutParams(params);
        }

        btnRemove.setOnClickListener( btnv ->{
            if(this.isRemovable && this.companionPosition != -1){
                listener.onRemoveCompanion(companionPosition);
            }
            this.dismiss();
        });

    }


    public interface CompanionDialogListener {
        void onCompanionAddConfirm(UserCompanion companion);
        void onRemoveCompanion(int position);
        void onCompanionEdited(UserCompanion companion, int position);
    }
}