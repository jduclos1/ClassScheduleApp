package com.example.jduclos1.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;


public class CourseNoteActEditor extends AppCompatActivity {
    private Uri cUri;
    private long cId;
    private Uri cNoteUri;
    private long cNoteId;
    private CourseNote cn;
    private EditText noteTextField;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_c_note_editor);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noteTextField = (EditText) findViewById(R.id.etCourseNoteText);

        cNoteUri = getIntent().getParcelableExtra(DataProv.C_NOTE_CONTENT_TYPE);
        if (cNoteUri == null) {
            setTitle("Enter New Note");
            cUri = getIntent().getParcelableExtra(DataProv.C_CONTENT_TYPE);
            cId = Long.parseLong(cUri.getLastPathSegment());
            action = Intent.ACTION_INSERT;
        }

        else {
            setTitle("Edit Note");
            cNoteId = Long.parseLong(cNoteUri.getLastPathSegment());
            cn = DataMgr.getCourseNote(this, cNoteId);
            cId = cn.cId;
            noteTextField.setText(cn.text);
            action = Intent.ACTION_EDIT;
        }
    }

    public void saveCourseNote(View view) {
        if (action == Intent.ACTION_INSERT) {
            DataMgr.insertCourseNote(this, cId, noteTextField.getText().toString().trim() );
            setResult(RESULT_OK);
            finish();
        }

        if (action == Intent.ACTION_EDIT) {
            cn.text = noteTextField.getText().toString().trim();
            cn.saveChanges(this);
            setResult(RESULT_OK);
            finish();
        }
    }
}
