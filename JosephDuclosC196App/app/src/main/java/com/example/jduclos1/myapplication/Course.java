package com.example.jduclos1.myapplication;

import android.content.ContentValues;
import android.content.Context;

public class Course {
    public long cId;
    public long tId;
    public String cName;
    public String cDesc;
    public String cStart;
    public String cEnd;
    public String cMentor;
    public String cMentorEmail;
    public String cMentorPhone;
    public boolean cNotifications;
    public Status cStatus;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.C_T_ID, tId);
        values.put(DatabaseMgr.C_NAME, cName);
        values.put(DatabaseMgr.C_DESC, cDesc);
        values.put(DatabaseMgr.C_START, cStart);
        values.put(DatabaseMgr.C_END, cEnd);
        values.put(DatabaseMgr.C_MENTOR, cMentor);
        values.put(DatabaseMgr.C_MENTOR_EMAIL, cMentorEmail);
        values.put(DatabaseMgr.C_MENTOR_PHONE, cMentorPhone);
        values.put(DatabaseMgr.C_NOTIFICATIONS, (cNotifications) ? 1 : 0 );
        values.put(DatabaseMgr.C_STATUS, cStatus.toString());

        context.getContentResolver().update(DataProv.C_URI, values, DatabaseMgr.C_TABLE_ID + "=" + cId, null);
    }
}
