package com.example.jduclos1.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TermActEditor extends AppCompatActivity implements View.OnClickListener {

    private static final int MAIN_CODE = 1;
    private String action;
    private String tFilter;
    private Term term;

    private EditText tName;
    private EditText tStart;
    private EditText tEnd;

    private DatePickerDialog tStartDialog;
    private DatePickerDialog tEndDialog;
    private SimpleDateFormat dateFormatter;

    private DataProv db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_t_editor);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DataProv();

        tName = (EditText) findViewById(R.id.termNameEditText);

        tStart = (EditText) findViewById(R.id.termStartDateEditText);
        tStart.setInputType(InputType.TYPE_NULL);

        tEnd = (EditText) findViewById(R.id.termEndDateEditText);
        tEnd.setInputType(InputType.TYPE_NULL);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DataProv.T_CONTENT_TYPE);

        // no uri means we're creating a new term
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.add_new_term));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle("Edit Term");
            long termId = Long.parseLong(uri.getLastPathSegment());
            term = DataMgr.getTerm(this, termId);
            fillTermForm(term);
        }

        setupDatePickers();
    }

    @Override
    public void onClick(View v) {
        if (v == tStart) {
            tStartDialog.show();
        }
        if (v == tEnd) {
            tEndDialog.show();
        }
    }

    private void fillTermForm(Term t) {
        tName.setText(t.tName);
        tStart.setText(t.tStart);
        tEnd.setText(t.tEnd);
    }

    private void getTermFromForm() {
        term.tName = tName.getText().toString().trim();
        term.tStart = tStart.getText().toString().trim();
        term.tEnd = tEnd.getText().toString().trim();
    }

    private void setupDatePickers() {
        tStart.setOnClickListener(this);
        tEnd.setOnClickListener(this);

        Calendar cal = Calendar.getInstance();
        tStartDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tStart.setText(dateFormatter.format(newDate.getTime()));
            }

        },cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        tEndDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tEnd.setText(dateFormatter.format(newDate.getTime()));
            }

        },cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        tStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    tStartDialog.show();
            }
        });

        tEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    tEndDialog.show();
            }
        });
    }

    public void saveTermChanges(View view) {
        if (action == Intent.ACTION_INSERT) {
            term = new Term();
            getTermFromForm();

            DataMgr.insertTerm(this,
                    term.tName,
                    term.tStart,
                    term.tEnd,
                    term.tStatus
            );

            // Notify that insert was completed
            Toast.makeText(this,
                    getString(R.string.term_saved),
                    Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);

        } else if (action == Intent.ACTION_EDIT) {
            getTermFromForm();
            term.saveChanges(this);
            // Notify that update was completed
            Toast.makeText(this,
                    getString(R.string.term_updated),
                    Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);
        }

        finish();
    }

}
