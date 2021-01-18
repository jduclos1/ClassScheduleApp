package com.example.jduclos1.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class CourseActViewer extends AppCompatActivity {

    private static final int COURSE_NOTE_ACT_LIST_CODE = 11111;
    private static final int ASSESS_ACT_LIST_CODE = 22222;
    private static final int COURSE_ACT_EDITOR_CODE = 33333;

    private Menu menu;
    private Uri cUri;
    private long cId;
    private Course course;

    private TextView tvCName;
    private TextView tvStart;
    private TextView tvEnd;
    private TextView tvStatus;

    private TextView tvMentor;
    private TextView tvMentorEmail;
    private TextView tvMentorPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_c_viewer);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        cUri = intent.getParcelableExtra(DataProv.C_CONTENT_TYPE);
        cId = Long.parseLong(cUri.getLastPathSegment());
        course = DataMgr.getCourse(this, cId);

        setStatusLabel();
        findElements();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_viewer, menu);
        this.menu = menu;
        showAppropriateMenuOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.edit_course:
                return editCourse();
            case R.id.del_course:
                return deleteCourse();
            case R.id.enable_notifications:
                return enableNotifications();
            case R.id.disable_notifications:
                return disableNotifications();
            case R.id.drop_course:
                return dropCourse();
            case R.id.start_course:
                return startCourse();
            case R.id.course_completed:
                return markCourseComplete();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void setStatusLabel() {
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        String status = "";

        switch(course.cStatus.toString()) {
            case "DROPPED":
                status = "Dropped";
                break;
            case "IN_PROGRESS":
                status = "In Progress";
                break;
            case "PLAN_TO_TAKE":
                status = "Plan to take";
                break;
            case "COMPLETED":
                status = "Completed";
                break;
        }

        tvStatus.setText("Status: " + status);

    }

    private void findElements() {
        tvCName = (TextView) findViewById(R.id.tvCourseName);
        tvStart = (TextView) findViewById(R.id.tvCourseStart);
        tvEnd = (TextView) findViewById(R.id.tvCourseEnd);

        tvMentor = (TextView) findViewById(R.id.tvCourseMentor);
        tvMentorEmail = (TextView) findViewById(R.id.tvCourseMentorEmail);
        tvMentorPhone = (TextView) findViewById(R.id.tvCourseMentorPhone);

        tvCName.setText(course.cName);
        tvStart.setText(course.cStart);
        tvEnd.setText(course.cEnd);

        tvMentor.setText(course.cMentor);
        tvMentorEmail.setText(course.cMentorEmail);
        tvMentorPhone.setText(course.cMentorPhone);

    }


    private void showAppropriateMenuOptions() {
        SharedPreferences sp = getSharedPreferences(Alarm.cAlarm, Context.MODE_PRIVATE);
        MenuItem item;

        menu.findItem(R.id.enable_notifications).setVisible(true);
        menu.findItem(R.id.disable_notifications).setVisible(true);

        if (course.cNotifications) {
            item = menu.findItem(R.id.enable_notifications);
        } else {
            item = menu.findItem(R.id.disable_notifications);
        }

        if (course.cStatus == null) {
            course.cStatus = Status.PLAN_TO_TAKE;
            course.saveChanges(this);
        }

        switch (course.cStatus.toString()) {
            case ("PLAN_TO_TAKE"):
                menu.findItem(R.id.drop_course).setVisible(false);
                menu.findItem(R.id.start_course).setVisible(true);
                menu.findItem(R.id.course_completed).setVisible(false);
                break;

            case ("COMPLETED"):
                menu.findItem(R.id.drop_course).setVisible(false);
                menu.findItem(R.id.start_course).setVisible(false);
                menu.findItem(R.id.course_completed).setVisible(false);
                break;

            case ("IN_PROGRESS"):
                menu.findItem(R.id.drop_course).setVisible(true);
                menu.findItem(R.id.start_course).setVisible(false);
                menu.findItem(R.id.course_completed).setVisible(true);
                break;

            case ("DROPPED"):
                menu.findItem(R.id.drop_course).setVisible(false);
                menu.findItem(R.id.start_course).setVisible(false);
                menu.findItem(R.id.course_completed).setVisible(false);
                break;
        }

        item.setVisible(false);
    }

    private boolean startCourse() {
        course.cStatus = Status.IN_PROGRESS;
        course.saveChanges(this);
        setStatusLabel();
        showAppropriateMenuOptions();
        return true;
    }

    private boolean editCourse() {
        Intent intent = new Intent(this, CourseActEditor.class);
        Uri uri = Uri.parse(DataProv.C_URI + "/" + course.cId);
        intent.putExtra(DataProv.C_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_ACT_EDITOR_CODE);
        return true;
    }

    private boolean dropCourse() {
        course.cStatus = Status.DROPPED;
        course.saveChanges(this);
        setStatusLabel();
        showAppropriateMenuOptions();
        return true;
    }

    public void openClassNotesList(View view) {
        Intent intent = new Intent(CourseActViewer.this, CourseNoteActList.class);
        Uri uri = Uri.parse(DataProv.C_URI + "/" + cId);
        intent.putExtra(DataProv.C_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_NOTE_ACT_LIST_CODE);
    }

    public void openAssessments(View view) {
        Intent intent = new Intent(CourseActViewer.this, AssessmentActList.class);
        Uri uri = Uri.parse(DataProv.C_URI + "/" + cId);
        intent.putExtra(DataProv.C_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESS_ACT_LIST_CODE);
    }

    private boolean markCourseComplete() {
        course.cStatus = Status.COMPLETED;
        course.saveChanges(this);
        setStatusLabel();
        showAppropriateMenuOptions();
        return true;
    }

    private boolean disableNotifications() {
        course.cNotifications = false;
        course.saveChanges(this);
        showAppropriateMenuOptions();
        return true;
    }

    private boolean enableNotifications() {
        long now = Date.todayLong();
        Long tsLong = System.currentTimeMillis();

        if (now <= Date.getDate(course.cStart)) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) cId, Date.getDate(course.cStart),
                    "Course starts Today!", course.cName + " begins on " + course.cStart);
        }
        if (now <= Date.getDate(course.cStart) - 3 * 24 * 60 * 60 * 1000) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) cId, Date.getDate(course.cStart) - 3 * 24 * 60 * 60 * 1000,
                    "Course starts in 3 days", course.cName + " begins on " + course.cStart);
        }
        if (now <= Date.getDate(course.cStart) - 24 * 60 * 60 * 1000) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) cId, Date.getDate(course.cStart) - 24 * 60 * 60 * 1000,
                    "Course starts tomorrow", course.cName + " begins on " + course.cStart);
        }

        if (now <= Date.getDate(course.cEnd)) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) cId, Date.getDate(course.cEnd),
                    "Course ends Today!", course.cName + " ends on " + course.cEnd);
        }
        if (now <= Date.getDate(course.cEnd) - 3 * 24 * 60 * 60 * 1000) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) cId, Date.getDate(course.cEnd) - 3 * 24 * 60 * 60 * 1000,
                    "Course ends in 3 days", course.cName + " ends on " + course.cEnd);
        }
        if (now <= Date.getDate(course.cEnd) - 24 * 60 * 60 * 1000) {
            Alarm.scheduleCourseAlarm(getApplicationContext(), (int) cId, Date.getDate(course.cEnd) - 24 * 60 * 60 * 1000,
                    "Course ends tomorrow", course.cName + " ends on " + course.cEnd);
        }

        Toast.makeText(this, "Alarms have been scheduled for this course.", Toast.LENGTH_SHORT).show();
        course.cNotifications = true;
        course.saveChanges(this);

        showAppropriateMenuOptions();

        return true;
    }

    private boolean deleteCourse() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    // Confirm that user wishes to proceed
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            DataMgr.deleteCourse(CourseActViewer.this, cId);
                            setResult(RESULT_OK);
                            finish();

                            // Notify that delete was completed
                            Toast.makeText(CourseActViewer.this,
                                    R.string.course_deld,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_del_course)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

        return true;
    }
}
