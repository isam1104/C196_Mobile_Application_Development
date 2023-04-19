package com.gitlab.schoolsystem;



import android.content.Context;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.ViewHolder> {

    private List<TermModel> termList = new ArrayList<>();
    private OnTermListener listener;
    private TermModelViewModel termModelViewModel;
    private Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.term,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TermModel termModel = termList.get(position);
        holder.term_name_view.setText(termModel.getTerm_name());
        holder.start_date_view.setText(termModel.getStart_date());
        holder.end_date_view.setText(termModel.getEnd_date());

        holder.update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current_position = holder.getAdapterPosition();
                if(listener != null && current_position != RecyclerView.NO_POSITION){
                    listener.onUpdateButtonClicked(termList.get(current_position));
                }
            }
        });

        holder.gridLayout.removeAllViews();
        // fillup the grid with courses
        termModelViewModel.getAllCoursesBelongingToTerm(termModel.getId()).observe((LifecycleOwner) context, new Observer<List<CourseModel>>() {
            @Override
            public void onChanged(List<CourseModel> courses) {
                if(courses != null)
                {
                    for(int i = 0; i < courses.size(); i++)
                    {
                        CourseModel course = courses.get(i);

                        TextView courseName = new TextView(context);
                        courseName.setText(course.getCourse_title() + " : ");
                        courseName.setGravity(Gravity.END);
                        courseName.setTypeface(null, Typeface.BOLD);
                        holder.gridLayout.addView(courseName, new GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.CENTER), GridLayout.spec(0)));

                        TextView startDate = new TextView(context);
                        startDate.setText(course.getCourse_start_date());
                        holder.gridLayout.addView(startDate, new GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.CENTER), GridLayout.spec(1)));

                        TextView to = new TextView(context);
                        to.setText(" to ");
                        holder.gridLayout.addView(to, new GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.CENTER), GridLayout.spec(2)));

                        TextView endDate  = new TextView(context);
                        endDate.setText(" " + course.getCourse_end_date());
                        holder.gridLayout.addView(endDate, new GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.CENTER), GridLayout.spec(3)));

                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return termList.size();
    }

    public void setTermList(List<TermModel> list){
        this.termList = list;
        notifyDataSetChanged();
    }

    public TermModel getTermAt(int position){
        return termList.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView term_name_view,
        start_date_view ,
        end_date_view;
        ImageButton update_btn;
        GridLayout gridLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            term_name_view = itemView.findViewById(R.id.termname);
            start_date_view = itemView.findViewById(R.id.startdate);
            end_date_view = itemView.findViewById(R.id.enddate);
            update_btn = itemView.findViewById(R.id.updatebtn);

            gridLayout = itemView.findViewById(R.id.this_grid);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int current_position = getAdapterPosition();
                    if(listener != null && current_position != RecyclerView.NO_POSITION){
                        listener.onTermClicked(termList.get(current_position));
                    }
                }
            });
        }
    }

    public interface OnTermListener{
        void onTermClicked(TermModel term);
        void onUpdateButtonClicked(TermModel term);
    }
    public void setListenerOnClick(OnTermListener listener){
        this.listener = listener;
    }

    public void setCourseGridRequirements(TermModelViewModel termModelViewModel, Context context) {
        this.termModelViewModel = termModelViewModel;
        this.context = context;
    }
}

