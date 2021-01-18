package com.example.jduclos1.myapplication;

import android.content.ContentValues;
import android.content.Context;

public class AssessmentNote {
    private long assessNoteId;
    public long assessId;
    public String text;

    public AssessmentNote(long assessmentNoteId) {
        this.assessNoteId = assessmentNoteId;
    }

    public void saveChanges(Context context) {
        ContentValues values = new ContentValues();
        values.put(DatabaseMgr.ASSESS_NOTE_TEXT, text);
        values.put(DatabaseMgr.ASSESS_NOTE_ASSESS_ID, assessId);


        context.getContentResolver().update(DataProv.ASSESS_NOTE_URI, values,
                DatabaseMgr.ASSESS_NOTE_TABLE_ID + "=" + assessNoteId, null);
    }
}
