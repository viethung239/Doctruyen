package com.example.truyentranh;

import android.app.Application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
//application class runs before your launcher activity
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public static final String formatTimestamp(long timestamp)
    {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());

        return formattedDate;
    }
}
