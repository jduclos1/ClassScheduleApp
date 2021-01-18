package com.example.jduclos1.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DataMgr {
    public static Uri insertTerm(Context context, String tName, String tStart, String tEnd, int tStatus) {
        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.T_NAME, tName);
        values.put(DatabaseMgr.T_START, tStart);
        values.put(DatabaseMgr.T_END, tEnd);
        values.put(DatabaseMgr.T_ACTIVE, tStatus);

        Uri tUri = context.getContentResolver().insert(DataProv.T_URI, values);
        Log.d("DataManager", "Inserted _Term: " + tUri.getLastPathSegment());

        return tUri;
    }

    public static Uri insertCourse(Context context, long tId, String cName, String cStart,
                                   String cEnd, String cMentor, String cMentorEmail,
                                   String cMentorPhone, Status status) {

        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.C_T_ID, tId);
        values.put(DatabaseMgr.C_NAME, cName);
        values.put(DatabaseMgr.C_START, cStart);
        values.put(DatabaseMgr.C_END, cEnd);
        values.put(DatabaseMgr.C_MENTOR, cMentor);
        values.put(DatabaseMgr.C_MENTOR_EMAIL, cMentorEmail);
        values.put(DatabaseMgr.C_MENTOR_PHONE, cMentorPhone);
        values.put(DatabaseMgr.C_STATUS, status.toString());

        Uri cUri = context.getContentResolver().insert(DataProv.C_URI, values);
        Log.d("DataManager", "Inserted Class: " + cUri.getLastPathSegment());

        return cUri;
    }

    public static Term getTerm(Context context, long tId) {
        Cursor cursor = context.getContentResolver().query(DataProv.T_URI, DatabaseMgr.T_COLS, 
                DatabaseMgr.T_TABLE_ID + "=" + tId, null, null);

        cursor.moveToFirst();
        String tName = cursor.getString(cursor.getColumnIndex(DatabaseMgr.T_NAME));
        String tStartDate = cursor.getString(cursor.getColumnIndex(DatabaseMgr.T_START));
        String tEndDate = cursor.getString(cursor.getColumnIndex(DatabaseMgr.T_END));
        int tStatus = cursor.getInt(cursor.getColumnIndex(DatabaseMgr.T_ACTIVE));

        Term t = new Term();
        t.tId = tId;
        t.tName = tName;
        t.tStart = tStartDate;
        t.tEnd = tEndDate;
        t.tStatus = tStatus;

        return t;
    }

    public static Course getCourse(Context context, long cId) {
        Cursor cursor = context.getContentResolver().query(DataProv.C_URI, DatabaseMgr.C_COLS,
                DatabaseMgr.C_TABLE_ID + " = " + cId, null, null);
        cursor.moveToFirst();
        Course c = new Course();

        c.cId = cursor.getLong(cursor.getColumnIndex(DatabaseMgr.C_TABLE_ID));
        c.cName = cursor.getString(cursor.getColumnIndex(DatabaseMgr.C_NAME));
        c.cStart = cursor.getString(cursor.getColumnIndex(DatabaseMgr.C_START));
        c.cEnd = cursor.getString(cursor.getColumnIndex(DatabaseMgr.C_END));
        c.cStatus = Status.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseMgr.C_STATUS)));
        c.cMentor = cursor.getString(cursor.getColumnIndex(DatabaseMgr.C_MENTOR));
        c.cMentorPhone = cursor.getString(cursor.getColumnIndex(DatabaseMgr.C_MENTOR_PHONE));
        c.cMentorEmail = cursor.getString(cursor.getColumnIndex(DatabaseMgr.C_MENTOR_EMAIL));
        c.cNotifications = (cursor.getInt(cursor.getColumnIndex(DatabaseMgr.C_NOTIFICATIONS)) == 1);

        return c;
    }

    public static boolean deleteCourse(Context context, long cId) {
        Cursor cursor = context.getContentResolver().query(DataProv.C_NOTE_URI, DatabaseMgr.C_NOTE_COLS, DatabaseMgr.C_NOTE_C_ID + "=" + cId, null, null );
        while (cursor.moveToNext()) {
            deleteCourseNote(context, cursor.getLong(cursor.getColumnIndex(DatabaseMgr.C_NOTE_TABLE_ID)));
        }

        context.getContentResolver().delete(DataProv.C_URI, DatabaseMgr.C_TABLE_ID + " = " + cId, null);

        return true;
    }

    public static Uri insertCourseNote(Context context, long cId, String text) {
        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.C_NOTE_C_ID, cId);
        values.put(DatabaseMgr.C_NOTE_TEXT, text);

        Uri cNoteUri = context.getContentResolver().insert(DataProv.C_NOTE_URI, values);
        Log.d("DataManager", "Inserted Course Note: " + cNoteUri.getLastPathSegment());

        return cNoteUri;
    }

    public static CourseNote getCourseNote(Context context, long cNoteId) {
        Cursor cursor = context.getContentResolver().query(DataProv.C_NOTE_URI, DatabaseMgr.C_NOTE_COLS, 
                DatabaseMgr.C_NOTE_TABLE_ID + "=" + cNoteId, null, null);
        cursor.moveToFirst();

        CourseNote c = new CourseNote(cNoteId);
        c.cId = cursor.getLong(cursor.getColumnIndex(DatabaseMgr.C_NOTE_C_ID));
        c.text = cursor.getString(cursor.getColumnIndex(DatabaseMgr.C_NOTE_TEXT));
        return c;
    }

    public static boolean deleteCourseNote(Context context, long cNoteId) {
        context.getContentResolver().delete(DataProv.C_NOTE_URI, DatabaseMgr.C_NOTE_TABLE_ID + " = " + cNoteId, null);
        return true;
    }

    public static Assessment getAssessment(Context context, long assessId) {
        Cursor cursor = context.getContentResolver().query(DataProv.ASSESS_URI, DatabaseMgr.ASSESS_COLS, 
                DatabaseMgr.ASSESS_TABLE_ID + "=" + assessId, null, null);

        cursor.moveToFirst();
        Assessment assess = new Assessment(assessId);
        assess.code = cursor.getString(cursor.getColumnIndex(DatabaseMgr.ASSESS_CODE));
        assess.cId = cursor.getLong(cursor.getColumnIndex(DatabaseMgr.ASSESS_C_ID));
        assess.name = cursor.getString(cursor.getColumnIndex(DatabaseMgr.ASSESS_NAME));
        assess.desc = cursor.getString(cursor.getColumnIndex(DatabaseMgr.ASSESS_DESC));
        assess.dateTime = cursor.getString(cursor.getColumnIndex(DatabaseMgr.ASSESS_DATE_TIME));
        assess.assessNotifications = cursor.getInt(cursor.getColumnIndex(DatabaseMgr.ASSESS_NOTIFICATIONS));
        return assess;
    }

    public static boolean deleteAssessment(Context context, long assessId) {
        Cursor cursor = context.getContentResolver().query(DataProv.ASSESS_NOTE_URI, DatabaseMgr.ASSESS_NOTE_COLS,
                DatabaseMgr.ASSESS_NOTE_ASSESS_ID + "=" + assessId, null, null );
        while (cursor.moveToNext()) {
            deleteAssessmentNote(context, cursor.getLong(cursor.getColumnIndex(DatabaseMgr.ASSESS_NOTE_TABLE_ID)));
        }

        context.getContentResolver().delete(DataProv.ASSESS_URI, DatabaseMgr.ASSESS_TABLE_ID + " = " + assessId, null);
        return true;
    }

    public static Uri insertAssessment(Context context, long cId, String code, String name, String desc, String dateTime) {
        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.ASSESS_C_ID, cId);
        values.put(DatabaseMgr.ASSESS_CODE, code);
        values.put(DatabaseMgr.ASSESS_NAME, name);
        values.put(DatabaseMgr.ASSESS_DESC, desc);
        values.put(DatabaseMgr.ASSESS_DATE_TIME, dateTime);

        Uri assessmentUri = context.getContentResolver().insert(DataProv.ASSESS_URI, values);
        Log.d("DataManager", "Inserted Assessment: " + assessmentUri.getLastPathSegment());

        return assessmentUri;
    }

    public static Uri insertAssessmentNote(Context context, long aId, String text) {
        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.ASSESS_NOTE_TEXT, text);
        values.put(DatabaseMgr.ASSESS_NOTE_ASSESS_ID, aId);

        Uri cUri = context.getContentResolver().insert(DataProv.ASSESS_NOTE_URI, values);
        Log.d("DataManager", "Inserted Assessment Note: " + cUri.getLastPathSegment());

        return cUri;
    }

    public static boolean deleteAssessmentNote(Context context, long assessNoteId) {
        context.getContentResolver().delete(DataProv.ASSESS_NOTE_URI, DatabaseMgr.ASSESS_NOTE_TABLE_ID + " = " + assessNoteId, 
                null);
        return true;
    }

    public static AssessmentNote getAssessmentNote(Context context, long cNoteId) {
        Cursor cursor = context.getContentResolver().query(DataProv.ASSESS_NOTE_URI, DatabaseMgr.ASSESS_NOTE_COLS, 
                DatabaseMgr.ASSESS_NOTE_TABLE_ID + "=" + cNoteId, null, null);

        cursor.moveToFirst();
        AssessmentNote c = new AssessmentNote(cNoteId);
        c.text = cursor.getString(cursor.getColumnIndex(DatabaseMgr.ASSESS_NOTE_TEXT));
        c.assessId = cursor.getLong(cursor.getColumnIndex(DatabaseMgr.ASSESS_NOTE_ASSESS_ID));
        return c;
    }
}
