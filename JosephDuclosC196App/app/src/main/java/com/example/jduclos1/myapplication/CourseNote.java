package com.example.jduclos1.myapplication;

import android.content.ContentValues;
import android.content.Context;

public class CourseNote {
    private long cNoteId;
    public long cId;
    public String text;

    public CourseNote(long cNoteId) {
        this.cNoteId = cNoteId;
    }

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.C_NOTE_TEXT, text);
        values.put(DatabaseMgr.C_NOTE_C_ID, cId);

        context.getContentResolver().update(DataProv.C_NOTE_URI, values, DatabaseMgr.C_NOTE_TABLE_ID + "=" + cNoteId, null);
    }
}
