package com.example.jduclos1.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;


public class Alarm extends BroadcastReceiver {

    public static final String cAlarm = "courseAlarms" ;
    public static final String assessAlarm = "assessmentAlarms";
    public static final String alarmFile = "alarmFile";
    public static final String nAlarmField = "nextAlarmId";

    @Override
    public void onReceive(Context context, Intent intent) {

        String destination = intent.getStringExtra("destination");
        if (destination == null || destination.isEmpty()) {
            destination = "";
        }

        int id = intent.getIntExtra("id", 0);
        String alarmtext = intent.getStringExtra("text");
        String alarmtitle = intent.getStringExtra("title");
        int nextAlarmId = intent.getIntExtra("nextAlarmId", getAndIncrementNextAlarmId(context));

        Notification.Builder mBuilder = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.alarmclock)
                        .setContentTitle(alarmtitle)
                        .setContentText(alarmtext);

        Intent resultIntent;
        Uri uri;
        SharedPreferences sPrefs;

        switch(destination) {
            case "course":
                Course course = DataMgr.getCourse(context, id);
                if (course != null && course.cNotifications) {
                    resultIntent = new Intent(context, CourseActViewer.class);
                    uri = Uri.parse(DataProv.C_URI + "/" + id);
                    resultIntent.putExtra(DataProv.C_CONTENT_TYPE, uri);
                } else {
                    return;
                }
                break;
            case "assessment":
                Assessment assessment = DataMgr.getAssessment(context, id);
                if (assessment != null && assessment.assessNotifications == 1) {
                    resultIntent = new Intent(context, AssessmentActViewer.class);
                    uri = Uri.parse(DataProv.ASSESS_URI + "/" + id);
                    resultIntent.putExtra(DataProv.ASSESS_CONTENT_TYPE, uri);
                } else {
                    return;
                }
                break;
            default:
                resultIntent = new Intent(context, Main.class);
                break;
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(Main.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationMgr.notify(nextAlarmId, mBuilder.build());
    }



    public static boolean scheduleCourseAlarm(Context context, int id, long time, String title, String text) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int nextAlarmId = getNextAlarmId(context);

        Intent intentAlarm = new Intent(context, Alarm.class);
        intentAlarm.putExtra("text", text);
        intentAlarm.putExtra("title", title);
        intentAlarm.putExtra("destination", "course");
        intentAlarm.putExtra("nextAlarmId", nextAlarmId);
        intentAlarm.putExtra("id", id);

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(context, nextAlarmId, intentAlarm, PendingIntent.FLAG_ONE_SHOT));

        SharedPreferences sp = context.getSharedPreferences(cAlarm, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Long.toString(id), nextAlarmId);
        editor.commit();

        incrementNextAlarmId(context);
        return true;
    }

    public static void deleteCourseAlarm(Context context, int id, String title, String text) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences sp = context.getSharedPreferences(cAlarm, Context.MODE_PRIVATE);

        Intent intentAlarm = new Intent(context, Alarm.class);
        intentAlarm.putExtra("text", text);
        intentAlarm.putExtra("title", title);
        intentAlarm.putExtra("destination", "assessment");


        if (sp.contains(Integer.toString(id))) {
            int mId = sp.getInt(Integer.toString(id), 0);
            if (mId > 0) {
                intentAlarm.putExtra("nextAlarmId", mId);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, mId, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }

            SharedPreferences.Editor editor = sp.edit();
            editor.remove(Integer.toString(id));
            editor.commit();
        }
    }

    public static boolean scheduleAssessmentAlarm(Context context, int id, long time, String title, String text) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int nextAlarmId = getNextAlarmId(context);

        Intent intentAlarm = new Intent(context, Alarm.class);
        intentAlarm.putExtra("text", text);
        intentAlarm.putExtra("title", title);
        intentAlarm.putExtra("destination", "assessment");
        intentAlarm.putExtra("nextAlarmId", nextAlarmId);
        intentAlarm.putExtra("id", id);

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));


        SharedPreferences sp = context.getSharedPreferences(assessAlarm, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Long.toString(id), nextAlarmId);
        editor.commit();

        incrementNextAlarmId(context);
        return true;
    }

    private static int getNextAlarmId(Context context) {
        SharedPreferences alarmPrefs;
        alarmPrefs = context.getSharedPreferences(alarmFile, Context.MODE_PRIVATE);
        int nextAlarmId = alarmPrefs.getInt(nAlarmField, 1);
        return nextAlarmId;
    }

    private static void incrementNextAlarmId(Context context) {
        SharedPreferences alarmPrefs;
        alarmPrefs = context.getSharedPreferences(alarmFile, Context.MODE_PRIVATE);
        int nextAlarmId = alarmPrefs.getInt(nAlarmField, 1);
        SharedPreferences.Editor alarmEditor = alarmPrefs.edit();
        alarmEditor.putInt(nAlarmField, nextAlarmId + 1);
        alarmEditor.commit();
    }

    private static int getAndIncrementNextAlarmId(Context context) {
        int nextAlarmId = getNextAlarmId(context);
        incrementNextAlarmId(context);
        return nextAlarmId;
    }
}
