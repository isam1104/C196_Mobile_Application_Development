package com.gitlab.schoolsystem;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePicker {
    private Calendar calendar = Calendar.getInstance();
    private Context context;
    private EditText date_view;
    public DatePicker(Context context, EditText date_view) {
        this.context = context;
        this.date_view = date_view;
    }

    public void enable( ) {
        date_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        date_view.setText(updateDate());
                    }
                };
                new DatePickerDialog(context, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();*/

                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .build();

                materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    String selectedDate = sdf.format(new Date(selection));
                    date_view.setText(selectedDate);
                });
                materialDatePicker.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE_PICKER");
            }
        });
    }

    private String updateDate() {
        String m_format = "yyyy-MM-dd";
        SimpleDateFormat date_format = new SimpleDateFormat(m_format, Locale.getDefault());
        return date_format.format(calendar.getTime());
    }
}
