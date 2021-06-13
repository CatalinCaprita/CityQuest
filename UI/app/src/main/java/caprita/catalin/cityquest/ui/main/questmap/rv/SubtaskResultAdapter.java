package caprita.catalin.cityquest.ui.main.questmap.rv;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.models.quest.UserSubtaskResultModel;

public class SubtaskResultAdapter extends RecyclerView.Adapter<SubtaskResultAdapter.ViewHolder> {

    private List<UserSubtaskResultModel> results;

    public SubtaskResultAdapter(List<UserSubtaskResultModel> results) {
        this.results = results;
        this.notifyDataSetChanged();
    }

    public List<UserSubtaskResultModel> getResults() {
        return results;
    }

    public void setResults(List<UserSubtaskResultModel> results) {
        this.results = results;
        this.notifyDataSetChanged();
    }

    @NonNull
    @io.reactivex.annotations.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @io.reactivex.annotations.NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_item_subtask_result, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull @io.reactivex.annotations.NonNull ViewHolder holder, int position) {
        UserSubtaskResultModel model = this.results.get(position);
        holder.tvSubtaskTitle.setText(model.getSubtaskContent());
        holder.tvCorrectAnswerContent.setText(model.getCorrectAnswerContent());
        if(model.getUserCorrect()){
            holder.tvUserAnswerContent.setText(R.string.user_answer_correct);
            holder.tvUserAnswerContent.setTextColor(holder.itemView.getResources().getColor(R.color.color_accent, null));
        }else {
            if (model.getUserAnswerContent() != null)
                holder.tvUserAnswerContent.setText(model.getUserAnswerContent());
            else
                holder.tvUserAnswerContent.setText(R.string.user_answer_nothing);
            holder.tvUserAnswerContent.setTextColor(holder.itemView.getResources().getColor(R.color.design_default_color_error, null));
        }

    }

    @Override
    public int getItemCount() {
        return this.results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        MaterialTextView tvSubtaskTitle, tvCorrectAnswerContent, tvUserAnswerContent;
        Color correctColor, wrongColor;
        public ViewHolder(@NonNull @io.reactivex.annotations.NonNull View itemView) {
            super(itemView);
            tvSubtaskTitle = itemView.findViewById(R.id.tv_subtask_title);
            tvCorrectAnswerContent = itemView.findViewById(R.id.tv_content_correct_answer);
            tvUserAnswerContent = itemView.findViewById(R.id.tv_content_user_answer);
        }
    }

}
