package com.example.jduclos1.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class CourseActEditor extends AppCompatActivity implements View.OnClickListener {

    private String action;
    private Course course;

    private Uri tUri;
    private Uri cUri;

    private EditText etCName;
    private EditText etCStart;
    private EditText etCEnd;
    private EditText etCMentor;
    private EditText etCMentorPhone;
    private EditText etCMentorEmail;

    private DatePickerDialog cStartDialog;
    private DatePickerDialog cEndDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_c_editor);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();

        Intent intent = getIntent();
        cUri = intent.getParcelableExtra(DataProv.C_CONTENT_TYPE);
        tUri = intent.getParcelableExtra(DataProv.T_CONTENT_TYPE);

        if (cUri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.add_new_course));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle("Edit Course");
            long classId = Long.parseLong(cUri.getLastPathSegment());
            course = DataMgr.getCourse(this, classId);
            fillCourseForm(course);
        }

        setupDatePickers();
    }

    @Override
    public void onClick(View v) {
        if (v == etCStart) {
            cStartDialog.show();
        }
        if (v == etCEnd) {
            cEndDialog.show();
        }
    }

    public void saveCourseChanges(View view) {
        if (action == Intent.ACTION_INSERT) {
            long termId = Long.parseLong(tUri.getLastPathSegment());
            DataMgr.insertCourse(this, termId,
                    etCName.getText().toString().trim(),
                    etCStart.getText().toString().trim(),
                    etCEnd.getText().toString().trim(),
                    etCMentor.getText().toString().trim(),
                    etCMentorEmail.getText().toString().trim(),
                    etCMentorPhone.getText().toString().trim(),
                    Status.PLAN_TO_TAKE
            );

            setResult(RESULT_OK);
        }
        else if (action == Intent.ACTION_EDIT) {
            course.cName = etCName.getText().toString().trim();
            course.cStart = etCStart.getText().toString().trim();
            course.cEnd = etCEnd.getText().toString().trim();
            course.cMentor = etCMentor.getText().toString().trim();
            course.cMentorEmail = etCMentorEmail.getText().toString().trim();
            course.cMentorPhone = etCMentorPhone.getText().toString().trim();

            course.saveChanges(this);
            setResult(RESULT_OK);
        }

        finish();
    }


    private void setupDatePickers() {
        etCStart.setOnClickListener(this);
        etCEnd.setOnClickListener(this);

        Calendar cal = Calendar.getInstance();
        cStartDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etCStart.setText(Date.dateFormat.format(newDate.getTime()));
            }

        },cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        cEndDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etCEnd.setText(Date.dateFormat.format(newDate.getTime()));
            }

        },cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        etCStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    cStartDialog.show();
            }
        });

        etCEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    cEndDialog.show();
            }
        });
    }
    
    private void fillCourseForm(Course c) {
        etCName.setText(c.cName);
        etCStart.setText(c.cStart);
        etCEnd.setText(c.cEnd);
        etCMentor.setText(c.cMentor);
        etCMentorEmail.setText(c.cMentorEmail);
        etCMentorPhone.setText(c.cMentorPhone);
    }

    private void findViews() {
        etCName = (EditText) findViewById(R.id.etCourseName);
        etCStart = (EditText) findViewById(R.id.etCourseStart);
        etCEnd = (EditText) findViewById(R.id.etCourseEnd);
        etCMentor = (EditText) findViewById(R.id.etCourseMentor);
        etCMentorEmail = (EditText) findViewById(R.id.etCourseMentorEmail);
        etCMentorPhone = (EditText) findViewById(R.id.etCourseMentorPhone);
    }
}
