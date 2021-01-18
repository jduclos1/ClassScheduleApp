package com.example.jduclos1.myapplication;

import android.content.ContentValues;
import android.content.Context;

public class Assessment {
    private long assessId;
    public String code;
    public String name;
    public String desc;
    public String dateTime;
    public long cId;
    public int assessNotifications;

    public Assessment(long id) {
        assessId = id;
    }

    public Assessment() {

    }

    public void saveChanges(Context context) {

        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.ASSESS_C_ID, cId);
        values.put(DatabaseMgr.ASSESS_CODE, code);
        values.put(DatabaseMgr.ASSESS_NAME, name);
        values.put(DatabaseMgr.ASSESS_DATE_TIME, dateTime);
        values.put(DatabaseMgr.ASSESS_DESC, desc);
        values.put(DatabaseMgr.ASSESS_NOTIFICATIONS, assessNotifications);

        context.getContentResolver().update(DataProv.ASSESS_URI, values,
                DatabaseMgr.ASSESS_TABLE_ID + "=" + assessId, null);
    }
}
