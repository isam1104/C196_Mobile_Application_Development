package com.gitlab.schoolsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.ViewHolder> {

    private static final String TAG = "AssessAdapter";

    private Context context;
    private List<AssessmentModel> assessmentModelList = new ArrayList<>();
    OnAssessmentListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assessment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssessmentModel assessmentModel = assessmentModelList.get(position);
        holder.assessment_name.setText(assessmentModel.getName());
        holder.assessment_start_date.setText(assessmentModel.getStart_date());
        holder.assessment_due_date.setText(assessmentModel.getDue_date());
        holder.assessment_type_view.setText(assessmentModel.getAssessmentType().toString());
        holder.assessment_delete_btn.setOnClickListener(view-> {
            int currentPosition = holder.getAdapterPosition();
            if(listener != null && currentPosition != RecyclerView.NO_POSITION)
            {
                listener.onDeleteClicked(assessmentModelList.get(currentPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return assessmentModelList.size();
    }

    public void setAssessmentModelList(List<AssessmentModel> assessmentModelList) {
        this.assessmentModelList = assessmentModelList;
        notifyDataSetChanged();
    }
    public AssessmentModel getModelAt(int position){
        return assessmentModelList.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView assessment_name, assessment_start_date,
        assessment_due_date, assessment_type_view;
        ImageButton assessment_delete_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            assessment_name = itemView.findViewById(R.id.assessment_title);
            assessment_start_date = itemView.findViewById(R.id.start_date);
            assessment_due_date = itemView.findViewById(R.id.due_date);
            assessment_type_view = itemView.findViewById(R.id.assessment_type_view);
            assessment_delete_btn = itemView.findViewById(R.id.assess_delete_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int current_position = getAdapterPosition();
                    if(listener != null && current_position != RecyclerView.NO_POSITION){
                        listener.onItemClicked(assessmentModelList.get(current_position));
                    }
                }
            });
        }
    }
    public interface OnAssessmentListener {
        void onItemClicked(AssessmentModel assessmentModel);
        void onDeleteClicked(AssessmentModel assessmentModel);
    }
    public void setAssessmentListenerOnClick(OnAssessmentListener listener){
        this.listener = listener;
    }
}
