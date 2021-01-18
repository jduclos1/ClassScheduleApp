package com.example.jduclos1.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;


public class AssessmentNoteActEditor extends AppCompatActivity {


    private Uri assessUri;
    private long assessId;

    private Uri assessNoteUri;
    private long assessNoteId;
    private AssessmentNote cn;

    private EditText noteTextField;

    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_assess_note_editor);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noteTextField = (EditText) findViewById(R.id.etAssessmentNoteText);

        assessNoteUri = getIntent().getParcelableExtra(DataProv.ASSESS_NOTE_CONTENT_TYPE);
        if (assessNoteUri == null) {
            // new Note
            setTitle("Enter New Note");
            assessUri = getIntent().getParcelableExtra(DataProv.ASSESS_CONTENT_TYPE);
            assessId = Long.parseLong(assessUri.getLastPathSegment());
            action = Intent.ACTION_INSERT;
        }

        else {
            setTitle("Edit Note");
            assessNoteId = Long.parseLong(assessNoteUri.getLastPathSegment());
            cn = DataMgr.getAssessmentNote(this, assessNoteId);
            assessId = cn.assessId;
            noteTextField.setText(cn.text);
            action = Intent.ACTION_EDIT;
        }
    }

    public void saveAssessmentNote(View view) {
        if (action == Intent.ACTION_INSERT) {
            DataMgr.insertAssessmentNote(this, assessId, noteTextField.getText().toString().trim() );
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
