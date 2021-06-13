package caprita.catalin.cityquest.ui.main.questmap.rv;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.quest.SnsSubtaskModel;
import caprita.catalin.cityquest.ui.models.quest.SubtaskModel;
import caprita.catalin.cityquest.ui.models.quest.UserSubtaskResultModel;

public class SnsSubtaskResultAdapter extends RecyclerView.Adapter<SnsSubtaskResultAdapter.ViewHolder> {

    private List<UserSubtaskResultModel> results;

    public SnsSubtaskResultAdapter(List<UserSubtaskResultModel> results) {
        this.results = results;
    }

    public List<UserSubtaskResultModel> getResults() {
        return results;
    }

    public void setResults(List<UserSubtaskResultModel> results) {
        this.results = results;
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SnsSubtaskResultAdapter.ViewHolder onCreateViewHolder(@NonNull @io.reactivex.annotations.NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_item_sns,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @io.reactivex.annotations.NonNull ViewHolder holder, int position) {
        UserSubtaskResultModel model = results.get(position);
        holder.tvItemDesc.setText(model.getCorrectAnswerContent());
//        holder.checkBox.setText(null);
        if(!model.getUserAnswerId().equals(-1L)){
//            mean user answered, so crossline the tv
            holder.tvItemDesc.setPaintFlags(holder.tvItemDesc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.checkBox.setChecked(true);
        }
        if(model.getUserCorrect()){
//            regardless of whether a user picked it, we check if its choice was correct
//            if so mark it as green
            holder.tvItemDesc.setTextColor(holder.itemView.getResources().getColor(R.color.color_accent, null));
        }else{
            holder.tvItemDesc.setTextColor(
                    holder.itemView.getResources().getColor(R.color.design_default_color_error, null));
        }
    }


    @Override
    public int getItemCount() {
        return this.results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        MaterialTextView tvItemDesc;

        public ViewHolder(@NonNull @io.reactivex.annotations.NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            tvItemDesc = itemView.findViewById(R.id.tv_item_desc);
            checkBox.setChecked(false);
            checkBox.setOnCheckedChangeListener(null);
        }
    }
}
