package com.example.jduclos1.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Term {
    public long tId;
    public String tName;
    public String tStart;
    public String tEnd;
    public int tStatus;

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.T_NAME, tName);
        values.put(DatabaseMgr.T_START, tStart);
        values.put(DatabaseMgr.T_END, tEnd);
        values.put(DatabaseMgr.T_ACTIVE, tStatus);
        context.getContentResolver().update(DataProv.T_URI, values, DatabaseMgr.T_TABLE_ID + "=" + tId, null);
    }

    public long getClassCount(Context context) {
        Cursor cursor = context.getContentResolver().query(DataProv.C_URI, DatabaseMgr.C_COLS,
                DatabaseMgr.C_T_ID+ "=" + this.tId, null, null );
        int numRows = cursor.getCount();
        return numRows;
    }

    public void activate(Context context) {
        this.tStatus = 1;
        saveChanges(context);
    }

    public void deactivate(Context context) {
        this.tStatus = 0;
        saveChanges(context);
    }

    
}
