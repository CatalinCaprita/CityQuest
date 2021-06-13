package caprita.catalin.cityquest.ui.main.questmap.rv;

import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.quest.SnsSubtaskModel;

public class SnsSubtaskAdapter extends RecyclerView.Adapter<SnsSubtaskAdapter.ViewHolder> {

    private List<SnsSubtaskModel> subtasks;
    private final OnSubtaskCheckedListener listener;
    private static final String TAG = "SnsSubtaskAdapter";

    public interface OnSubtaskCheckedListener{
        void onSubtaskChecked(int subtaskIndex, Long uniqueAnswerId);
        void onSubtaskUnchecked(int subtaskIndex);
    }
    public SnsSubtaskAdapter(List<SnsSubtaskModel> subtasks,
                             OnSubtaskCheckedListener listener) {
        this.subtasks = subtasks;
        this.listener = listener;
    }

    public List<SnsSubtaskModel> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<SnsSubtaskModel> subtasks) {
        this.subtasks = subtasks;
        notifyDataSetChanged();
    }

    public OnSubtaskCheckedListener getListener() {
        return listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @io.reactivex.annotations.NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_item_sns,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @io.reactivex.annotations.NonNull ViewHolder holder, int position) {
        SnsSubtaskModel model = subtasks.get(position);
        holder.tvItemDesc.setText(model.getUniqueAnswerContent());
//        holder.checkBox.setText(null);
        holder.checkBox.setChecked(!model.getUserAnswerId().equals(-1L));
        holder.uniqueAnswerId = model.getUniqueAnswerId();
    }

    @Override
    public int getItemCount() {
        return this.subtasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        MaterialTextView tvItemDesc;
        Long uniqueAnswerId;
        public ViewHolder(@NonNull @io.reactivex.annotations.NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            tvItemDesc = itemView.findViewById(R.id.tv_item_desc);
            checkBox.setChecked(false);
//            checkBox.setText(null);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        listener.onSubtaskChecked(getAdapterPosition(), uniqueAnswerId);
                        tvItemDesc.setPaintFlags(tvItemDesc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        if(getAdapterPosition() != RecyclerView.NO_POSITION){
                            SnsSubtaskModel model = subtasks.get(getAdapterPosition());
                            Log.d(TAG, "onCheckedChanged: Setting Index" + getAdapterPosition() + " to Checked");
                            model.setUserAnswerId(model.getUniqueAnswerId());
                        }
                    }
                    else {
                        listener.onSubtaskUnchecked(getAdapterPosition());
                        tvItemDesc.setPaintFlags(tvItemDesc.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });
        }
    }
}
