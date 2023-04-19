package com.gitlab.schoolsystem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseActivity extends AppCompatActivity{
    public static final String TERM_MODEL =
            "com.gitlab.schoolsystem.TermModel";
    public static final String COURSE_MODEL =
            "com.gitlab.schoolsystem.CourseModel";
    private static final String TAG = "CourseActivity";
    private static final int NUM_COLS  = 2;

    private TermModel termModel;
    private CourseModel courseModel;

    private CourseModelViewModel courseModelViewModel;
    private CourseAdapter courseAdapter;


    @Override
    public boolean onSupportNavigateUp() {
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_courses);

        if(getIntent().hasExtra(CourseActivity.TERM_MODEL)){
            termModel = getIntent().getParcelableExtra(CourseActivity.TERM_MODEL);
        }
        // set up a back button on the toolbar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            if(termModel != null){
                actionBar.setTitle(termModel.getTerm_name());
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // init recyclerview
        RecyclerView recyclerView = findViewById(R.id.courses_recycler_view);
        courseAdapter = new CourseAdapter();
        courseAdapter.setTermModel(termModel);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(courseAdapter);


        courseModelViewModel = ViewModelProviders.of(this).get(CourseModelViewModel.class);
        courseModelViewModel.getCoursesByTermID(termModel.getId()).observe(this, courseModels -> courseAdapter.setCourseModelList(courseModels));

        // add dialog
        FloatingActionButton add_course_btn = findViewById(R.id.add_course);
        add_course_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<CourseModel> temp_courses =  courseModelViewModel.getAllCourses().getValue();
                new CourseDialog(CourseActivity.this, courseModelViewModel, temp_courses, termModel).show();
            }
        });
        // course listeners
        courseAdapter.setCourseListerOnClick(new CourseAdapter.OnCourseListener() {
            @Override
            public void onCourseClicked(CourseModel course) {
                Log.d(TAG, "onCourseClicked: " + course.toString());
                Intent intent = new Intent(CourseActivity.this, CourseChildrenActivity.class);
                intent.putExtra(CourseActivity.COURSE_MODEL, course);
                intent.putExtra(CourseActivity.TERM_MODEL, termModel);
                startActivity(intent);
            }


            @Override
            public void onDeleteClicked(CourseModel course) {
                // create a delete confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(CourseActivity.this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Operation cannot be undone.");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // deleted selected course
                        courseModelViewModel.delete(course);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CourseActivity.this, "Delete canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }

            @Override
            public void onInstructorClicked(CourseModel course) {
                new InstructorDialog(CourseActivity.this, courseModelViewModel, course).show();
            }

            @Override
            public void onAlarmPickerClicked(CourseModel course) {
                MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder
                        .dateRangePicker()
                        .setTitleText("Select notification dates for both start and end dates")
                        .setSelection(new Pair<>(System.currentTimeMillis(), System.currentTimeMillis()))
                        .build();

                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        String startDate = sdf.format(new Date(selection.first));
                        String endDate = sdf.format(new Date(selection.second));
                        String startMessage = "Your " + course.getCourse_title() + " will start on " + course.getCourse_start_date();
                        String endMessage = "Your " + course.getCourse_title() + " will end on " + course.getCourse_end_date();
                        Utils.scheduleNotification(getApplicationContext(), startDate, startMessage);
                        Utils.scheduleNotification(getApplicationContext(), endDate, endMessage);
                    }
                });

                picker.show(getSupportFragmentManager(), picker.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update the course in the new activity
        if(getIntent().hasExtra(CourseActivity.COURSE_MODEL)){
            courseModel = getIntent().getParcelableExtra(CourseActivity.COURSE_MODEL);
            courseModel.setTerm_id(termModel.getId());
            Log.d(TAG, "onResume: " + courseModel.toString());
            courseModelViewModel.update(courseModel);
            courseAdapter.notifyDataSetChanged();
        }
    }
}