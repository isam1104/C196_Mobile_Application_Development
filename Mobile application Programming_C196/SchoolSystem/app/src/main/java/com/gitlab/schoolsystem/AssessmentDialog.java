package com.gitlab.schoolsystem;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class AssessmentDialog {
    private Context context;
    private AssessmentModelViewModel assessmentModelViewModel;
    private AssessmentModel assessmentModel;
    private boolean isInserting;
    private CourseModel courseModel;

    public AssessmentDialog(Context context, AssessmentModelViewModel assessmentModelViewModel, AssessmentModel assessmentModel, CourseModel courseModel,boolean isInserting){
        this.context = context;
        this.assessmentModelViewModel = assessmentModelViewModel;
        this.isInserting = isInserting;
        this.assessmentModel = assessmentModel;
        this.courseModel = courseModel;
    }

    public void show() {
        // Note dialog  elements
        View view = LayoutInflater.from(context).inflate(R.layout.assessment_dialog, null);
        EditText assessment_title = view.findViewById(R.id.assessment_title_edit);
        EditText assessment_start_date = view.findViewById(R.id.start_date_edit_2);
        EditText assessment_due_date = view.findViewById(R.id.due_date_edit_2);

        new DatePicker(context, assessment_start_date).enable();
        new DatePicker(context, assessment_due_date).enable();

        Spinner assessment_type_spinner = view.findViewById(R.id.assessment_type);
        String[] assessment_resource_array = context.getResources().getStringArray(R.array.assessment_types);
        AtomicReference<String> selected_assessment_type  = new AtomicReference<>();
        setupSpinner(assessment_type_spinner, assessment_resource_array, (pair)->{
            Spinner spinner = pair.first;
            selected_assessment_type.set(pair.second);
        } );
        // update elements
        if(!isInserting && assessmentModel != null) {
            // requires update
            assessment_title.setText(assessmentModel.getName());
            assessment_start_date.setText(assessmentModel.getStart_date());
            assessment_due_date.setText(assessmentModel.getDue_date());
            assessment_type_spinner.setSelection(assessmentModel.getAssessmentType().ordinal());
        }
        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setTitle("Assessment details")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!Utils.isEmpty(assessment_title) && !Utils.isEmpty(assessment_due_date)){
                            String start_date = assessment_start_date.getText().toString();
                            String due_date = assessment_due_date.getText().toString();
                            String assess_title = assessment_title.getText().toString();
                            if(Utils.compareDates(start_date, due_date) >= 0)
                            {
                                Toast.makeText(context, "Wrong dates order", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(!Utils.isWithinDates(courseModel.getCourse_start_date(), courseModel.getCourse_end_date(), start_date,due_date))
                            {
                                Toast.makeText(context, "Assessment dates are NOT within course dates", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(isInserting){
                                // insert model
                                AssessmentModel new_model = new AssessmentModel(assess_title,start_date,due_date, AssessmentType.valueOf(selected_assessment_type.toString()));
                                new_model.setCourse_id(courseModel.getId());
                                assessmentModelViewModel.insert(new_model);
                            }else{
                                // update  the model
                                assessmentModel.setName(assess_title);
                                assessmentModel.setStart_date(start_date);
                                assessmentModel.setDue_date(due_date);
                                assessmentModel.setAssessmentType(AssessmentType.valueOf(selected_assessment_type.toString()));
                                assessmentModelViewModel.update(assessmentModel);
                            }
                            // schedule notifications
                            scheduleNotification(start_date, "Your " + assess_title + " is starting on " + start_date);
                            scheduleNotification(due_date, "Your " + assess_title + " is due on " + due_date);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_corners_background);
        dialog.show();
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
            Log.d(TAG, "scheduleNotification: " +  e.getMessage());
        }
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
}
