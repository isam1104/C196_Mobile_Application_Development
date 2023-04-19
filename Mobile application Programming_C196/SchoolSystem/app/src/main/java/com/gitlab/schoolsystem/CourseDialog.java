package com.gitlab.schoolsystem;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CourseDialog {
    private Context context;
    private CourseModelViewModel courseModelViewModel;
    private List<CourseModel> courseModels;
    private TermModel termModel;

    // setup notification
    private NotificationManagerCompat notificationManager;

    public CourseDialog(Context context, CourseModelViewModel courseModelViewModel, List<CourseModel> courseModels, TermModel termModel){
        this.context = context;
        this.courseModelViewModel = courseModelViewModel;
        this.courseModels = courseModels;
        this.termModel = termModel;
        this.notificationManager = NotificationManagerCompat.from(context);
    }
    public void show() {
        View course_dialog_view = LayoutInflater.from(context).inflate(R.layout.course_dialog, null);
        // Course title
        EditText course_title_view = course_dialog_view.findViewById(R.id.coursetitle_edit);
        // course start date
        EditText course_start_date = course_dialog_view.findViewById(R.id.startdate_edit);
        new DatePicker(context, course_start_date).enable();
        // course end date
        EditText course_end_date = course_dialog_view.findViewById(R.id.enddate_edit);
        new DatePicker(context, course_end_date).enable();
        //  course status spinner
        Spinner status_spinner = course_dialog_view.findViewById(R.id.status_spinner);
        String[] status_resource_array = context.getResources().getStringArray(R.array.course_status_list);
        AtomicReference<String> selected_status  = new AtomicReference<>();
        setupSpinner(status_spinner, status_resource_array, (pair)->{
            Spinner spinner = pair.first;
            selected_status.set(pair.second);
        } );
        // instructor spinner
        Spinner instructor_spinner = course_dialog_view.findViewById(R.id.instructor_spinner);
        String[] instructor_resource_array =  getInstructorNames();
        AtomicReference<String> selected_instructor = new AtomicReference<>();
        setupSpinner(instructor_spinner, instructor_resource_array, (pair)->{
            Spinner spinner = pair.first;
            selected_instructor.set(pair.second);
        });
        // builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(course_dialog_view);
        builder.setTitle("Enter the course details")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!Utils.isEmpty(course_title_view) && !Utils.isEmpty(course_start_date) && !Utils.isEmpty(course_start_date)){
                            String start_date_ = course_start_date.getText().toString();
                            String end_date_ = course_end_date.getText().toString();
                            if(Utils.compareDates(start_date_, end_date_) < 0){
                                // validate dates - should be within the dates of the term
                                if(!Utils.isWithinDates(termModel.getStart_date(), termModel.getEnd_date(), start_date_, end_date_))
                                {
                                    Toast.makeText(context, "Course dates are NOT within the term dates", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                scheduleNotification(start_date_, "Your " + course_title_view.getText().toString() + " starts on " +  start_date_);
                                scheduleNotification(end_date_, "Your " + course_title_view.getText().toString() + " ends on " +  end_date_);
                                CourseModel  new_course_model = new CourseModel(
                                        course_title_view.getText().toString(),
                                        start_date_,
                                        end_date_,
                                        CourseStatus.valueOf(selected_status.toString()),
                                        new InstructorModel(selected_instructor.toString()));
                                new_course_model.setTerm_id(termModel.getId());// set the term name for this model
                                courseModelViewModel.insert(new_course_model);
                            }else {
                                Toast.makeText(context, "Wrong dates order", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_corners_background);
        dialog.show();
    }

    private String[] getInstructorNames() {
        List<String> instructors = new ArrayList<>();
        if(courseModels == null ){
            // return an empty instructor
            instructors.add(new InstructorModel("Instructor", "instructor@gmail.com", "0987654321").toString());
            return instructors.toArray(new String[0]);
        }
        for(CourseModel courseModel: courseModels){
            instructors.add(courseModel.getCourse_instructor().toString());
        }
        return instructors.toArray(new String[0]);
    }

    private void setupSpinner(final Spinner spinner , String[] resource_array, Consumer<Pair<Spinner, String>> callback){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                R.layout.custom_spinner,
                resource_array
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spinner.getItemAtPosition(i).toString();
                callback.accept(new Pair<>(spinner, selectedItem));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void scheduleNotification(String dateString, String message) {
        Toast.makeText(context,"Scheduling notification for "+dateString , Toast.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = dateFormat.parse(dateString);
            if(date == null)
            {
                throw new Exception("Date is null");
            }
            calendar.setTime(date);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("message",  message);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            pendingIntent.send();
        }catch (Exception e)
        {
            Log.d(TAG, "Error scheduleNotification: " +  e.getMessage());
        }
    }
}


