package caprita.catalin.cityquest.ui.main.companion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.UserCompanion;

public class CompanionAdapter extends RecyclerView.Adapter<CompanionAdapter.ViewHolder> {

    private List<UserCompanion> companions;
    private final OnCompanionClickListener listener;
    public CompanionAdapter(List<UserCompanion> companions,
                            OnCompanionClickListener listener) {
        this.companions = companions;
        this.listener = listener;
    }

    public void setCompanions(List<UserCompanion> companions) {
        this.companions = companions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_item_companion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserCompanion comp = this.companions.get(position);
        holder.ivProfile.setImageResource(R.drawable.ic_baseline_person_24);
        holder.tvName.setText(comp.getName());
        holder.tvDescription.setText(String.format(holder.tvDescription.getText().toString(),
                comp.getNickname()));
    }

    @Override
    public int getItemCount() {
        return this.companions.size();
    }

    /**
     * ViewHolder that will manage the View associated with each user companion*/
    public class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView ivProfile;
        TextView tvName;
        TextView tvDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_quest_location);
            tvName = itemView.findViewById(R.id.tv_companion_name);
            tvDescription = itemView.findViewById(R.id.tv_companion_attribute);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION)
                        listener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnCompanionClickListener{
        void onClick(int position);
    }
}
