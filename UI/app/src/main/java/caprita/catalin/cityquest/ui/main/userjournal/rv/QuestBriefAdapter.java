package caprita.catalin.cityquest.ui.main.userjournal.rv;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.quest.QuestBriefModel;

public class QuestBriefAdapter extends RecyclerView.Adapter<QuestBriefAdapter.ViewHolder> {

    private List<QuestBriefModel> questBriefs;
    private final OnQuestClickListener listener;
    public QuestBriefAdapter(List<QuestBriefModel> questBriefs,
                             OnQuestClickListener listener) {
        this.questBriefs = questBriefs;
        this.listener = listener;
    }

    public List<QuestBriefModel> getQuestBriefs() {
        return questBriefs;
    }

    public void setQuestBriefs(List<QuestBriefModel> questBriefs) {
        this.questBriefs = questBriefs;
        notifyDataSetChanged();
    }

    @NonNull
    @io.reactivex.annotations.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @io.reactivex.annotations.NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_item_quest_brief, parent,
                false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @io.reactivex.annotations.NonNull ViewHolder holder, int position) {
        QuestBriefModel model = this.questBriefs.get(position);
        holder.tvQuestTitle.setText(model.getTitle());
        Resources r = holder.itemView.getResources();
        holder.tvQuestType.setText(r.getString(R.string.quest_card_type_template, model.getType()));
        holder.tvQuestLocation.setText(r.getString(R.string.quest_card_location_template, model.getLocationName()));
        holder.tvPrimaryRwd.setText(String.format(holder.tvPrimaryRwd.getText().toString(),
                model.getPrimaryRewardAmount(),
                model.getPrimaryRewardType()
                ));

        holder.tvSecondaryRwd.setText(String.format(holder.tvSecondaryRwd.getText().toString(),
                model.getSecondaryRewardAmount(),
                model.getSecondaryRewardType()
        ));
        holder.ivLocation.setImageResource(model.getResourceId());
        holder.questId = model.getId();
    }

    @Override
    public int getItemCount() {
        return questBriefs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView ivLocation;
        MaterialTextView tvQuestTitle, tvQuestLocation, tvQuestType, tvPrimaryRwd, tvSecondaryRwd;
        Long questId;
        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            tvQuestTitle = itemView.findViewById(R.id.tv_quest_title);
            tvQuestLocation = itemView.findViewById(R.id.tv_quest_location);
            tvQuestType = itemView.findViewById(R.id.tv_quest_type);
            tvPrimaryRwd = itemView.findViewById(R.id.tv_primary_rwd);
            tvSecondaryRwd = itemView.findViewById(R.id.tv_secondary_rwd);
            ivLocation = itemView.findViewById(R.id.iv_quest_location);
            itemView.setOnClickListener( view->{
                listener.onQuestClick(questId);
            });

        }
    }
    public interface OnQuestClickListener{
        void onQuestClick(Long questId);
    }
}
