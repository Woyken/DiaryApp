package com.uzkuraitis.karolis.diaryapp;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by karol on 11/13/2017.
 */

public class ReminderAlarmReceiver extends BroadcastReceiver {
    String TAG = "AlarmReceiver";




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override

    public void onReceive(Context context, Intent intent) {

        //Trigger the notification
        int id = intent.getIntExtra("ReminderId", 0);
        String title = intent.getStringExtra("ReminderTitle");
        String content = intent.getStringExtra("ReminderContent");
        NotificationScheduler.showNotification(context, MainCalendarActivity.class, id, title, content);

    }
}
