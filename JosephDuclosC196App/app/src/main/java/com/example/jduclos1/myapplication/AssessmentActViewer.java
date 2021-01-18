package com.example.jduclos1.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class AssessmentActViewer extends AppCompatActivity {

    private static final int ASSESS_ACT_EDITOR_CODE = 11111;
    private static final int ASSESS_NOTE_ACT_LIST_CODE = 22222;
    private long assessId;

    private Assessment assess;

    private TextView tvAssessTitle;
    private TextView tvAssessDesc;
    private TextView tvAssessDate;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_assess_viewer);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentActViewer.this, AssessmentActEditor.class);
                Uri uri = Uri.parse(DataProv.ASSESS_URI + "/" + assessId);
                intent.putExtra(DataProv.ASSESS_CONTENT_TYPE, uri);
                startActivityForResult(intent, ASSESS_ACT_EDITOR_CODE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadAssessment();
    }

    private void loadAssessment() {
        Uri assessUri = getIntent().getParcelableExtra(DataProv.ASSESS_CONTENT_TYPE);
        assessId = Long.parseLong(assessUri.getLastPathSegment());
        assess = DataMgr.getAssessment(this, assessId);

        tvAssessTitle = (TextView) findViewById(R.id.tvAssessmentTitle);
        tvAssessDesc = (TextView) findViewById(R.id.tvAssessmentDescription);
        tvAssessDate = (TextView) findViewById(R.id.tvAssessmentDatetime);


        tvAssessTitle.setText(assess.code + ": " + assess.name);
        tvAssessDesc.setText(assess.desc);
        tvAssessDate.setText(assess.dateTime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadAssessment();
        }
    }

    public void openAssessmentNotesList(View view) {
        Intent intent = new Intent(AssessmentActViewer.this, AssessmentNoteActList.class);
        Uri uri = Uri.parse(DataProv.ASSESS_URI + "/" + assessId);
        intent.putExtra(DataProv.ASSESS_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESS_NOTE_ACT_LIST_CODE);
    }


    /// Setup menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment_viewer, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.del_assess:
                return deleteAssessment();
            case R.id.enable_notifications:
                return enableNotifications();
            case R.id.disable_notifications:
                return disableNotifications();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean deleteAssessment() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    // Confirm that user wishes to proceed
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            DataMgr.deleteAssessment(AssessmentActViewer.this, assessId);
                            setResult(RESULT_OK);
                            finish();

                            // Notify that delete was completed
                            Toast.makeText(AssessmentActViewer.this,
                                    R.string.assessment_deld,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_del_assessment)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

        return true;
    }

    private void showAppropriateMenuOptions() {
        SharedPreferences sp = getSharedPreferences(Alarm.cAlarm, Context.MODE_PRIVATE);
        MenuItem item;

        menu.findItem(R.id.enable_notifications).setVisible(true);
        menu.findItem(R.id.disable_notifications).setVisible(true);

        if (assess.assessNotifications == 1) {
            menu.findItem(R.id.enable_notifications).setVisible(false);
        } else {
            menu.findItem(R.id.disable_notifications).setVisible(false);
        }
    }

    private boolean enableNotifications() {
        long now = Date.todayLong();
        Long tsLong = System.currentTimeMillis();

        Alarm.scheduleAssessmentAlarm(getApplicationContext(), (int) assessId, 
                System.currentTimeMillis() + 1000, "Assessment Notification.", assess.name + " takes place on " + assess.dateTime);

        if (now <= Date.getDate(assess.dateTime)) {
            Alarm.scheduleAssessmentAlarm(getApplicationContext(), (int) assessId, Date.getDate(assess.dateTime), 
                    "Assessment", assess.name + " scheduled for " + assess.dateTime);
        }
        if (now <= Date.getDate(assess.dateTime) - 3 * 24 * 60 * 60 * 1000) {
            Alarm.scheduleAssessmentAlarm(getApplicationContext(), (int) assessId, 
                    Date.getDate(assess.dateTime) - 3 * 24 * 60 * 60 * 1000, "Assessment in 3 days!",
                    assess.name + " scheduled for " + assess.dateTime);
        }
        if (now <= Date.getDate(assess.dateTime) - 24 * 60 * 60 * 1000) {
            Alarm.scheduleAssessmentAlarm(getApplicationContext(), (int) assessId, 
                    Date.getDate(assess.dateTime) - 24 * 60 * 60 * 1000, "Assessment is tomorrow!",
                    assess.name + " scheduled for " + assess.dateTime);
        }
        Toast.makeText(this, "Alarm scheduled for this Assessment.", Toast.LENGTH_SHORT).show();
        assess.assessNotifications = 1;
        assess.saveChanges(this);
        showAppropriateMenuOptions();
        return true;
    }

    private boolean disableNotifications() {
        assess.assessNotifications = 0;
        assess.saveChanges(this);
        showAppropriateMenuOptions();
        return true;
    }
}
