package com.example.jduclos1.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;


public class AssessmentActEditor extends AppCompatActivity {

    private Assessment assess;
    private long cId;
    private String action;
    private EditText assessName;
    private EditText assessDesc;
    private EditText assessDateTime;
    private EditText assessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_assess_editor);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assessCode = (EditText) findViewById(R.id.etAssessmentCode);
        assessName = (EditText) findViewById(R.id.etAssessmentName);
        assessDesc = (EditText) findViewById(R.id.etAssessmentDescription);
        assessDateTime = (EditText) findViewById(R.id.etAssessmentDatetime);


        Uri assessUri = getIntent().getParcelableExtra(DataProv.ASSESS_CONTENT_TYPE);
        if (assessUri == null) {
            // new Assessment
            setTitle(getString(R.string.new_assessment));
            action = Intent.ACTION_INSERT;
            Uri courseUri = getIntent().getParcelableExtra(DataProv.C_CONTENT_TYPE);
            cId = Long.parseLong(courseUri.getLastPathSegment());
            assess = new Assessment();
        }
        else {
            // existing assess
            setTitle(getString(R.string.edit_assessment));
            action = Intent.ACTION_EDIT;
            Long assessId = Long.parseLong(assessUri.getLastPathSegment());
            assess = DataMgr.getAssessment(this, assessId);
            cId = assess.cId;
            populateFields();
        }
    }

    private void getValuesFromFields() {
        assess.code = assessCode.getText().toString().trim();
        assess.dateTime = assessDateTime.getText().toString().trim();
        assess.desc = assessDesc.getText().toString().trim();
        assess.name = assessName.getText().toString().trim();
    }

    public void saveAssessmentChanges(View view) {
        getValuesFromFields();
        switch (action) {
            case Intent.ACTION_INSERT:
                DataMgr.insertAssessment(this, cId, assess.code, assess.name,
                        assess.desc, assess.dateTime);
                setResult(RESULT_OK);
                finish();
                break;

            case Intent.ACTION_EDIT:
                assess.saveChanges(this);
                setResult(RESULT_OK);
                finish();
                break;

            default:
                throw new UnsupportedOperationException();
        }
    }

    private void populateFields() {
        if (assess != null) {
            assessCode.setText(assess.code);
            assessDateTime.setText(assess.dateTime);
            assessName.setText(assess.name);
            assessDesc.setText(assess.desc);
        }
    }
}
