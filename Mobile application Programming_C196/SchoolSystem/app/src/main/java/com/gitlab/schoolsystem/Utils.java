package com.gitlab.schoolsystem;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static boolean isEmpty(EditText field){
        boolean is_empty = TextUtils.isEmpty(field.getText());
        if(is_empty){
            field.setError("This field is required");
            Toast.makeText(field.getContext(), "Empty field/s" , Toast.LENGTH_LONG).show();
        }else{
            field.setError(null);
        }
        return is_empty;
    }
    public static int compareDates(String date1, String date2) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date firstDate = sdf.parse(date1);
            Date secondDate = sdf.parse(date2);
            return firstDate.compareTo(secondDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static Calendar getCalendarInstance(String date_string){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(date_string);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
    public static boolean isWithinDates(String UDate, String LDate, String date1, String date2)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date uDate = sdf.parse(UDate);
            Date lDate = sdf.parse(LDate);
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);

            return (uDate.before(d1) || uDate.equals(d1)) && (lDate.after(d2) || lDate.equals(d2));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void scheduleNotification(Context context, String dateString, String message) {
        Toast.makeText(context,"Scheduled notification for "+dateString , Toast.LENGTH_SHORT).show();

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

}
