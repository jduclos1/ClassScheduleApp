package com.example.jduclos1.myapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.GregorianCalendar;

public class TermActList extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int T_ACT_EDITOR_CODE = 11111;
    public static final int T_ACT_VIEWER_CODE = 22222;

    private CursorAdapter ca;
    private DataProv db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_t_list);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] from = { DatabaseMgr.T_NAME, DatabaseMgr.T_START, DatabaseMgr.T_END };
        int[] to = { R.id.tvTerm, R.id.tvTermStartDate, R.id.tvTermEndDate };

        ca = new SimpleCursorAdapter(this, R.layout.t_list_item, null, from, to, 0);
        db = new DataProv();

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(ca);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermActList.this, TermActViewer.class);
                Uri uri = Uri.parse(DataProv.T_URI + "/" + id);
                intent.putExtra(DataProv.T_CONTENT_TYPE, uri);
                startActivityForResult(intent, T_ACT_VIEWER_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            restartLoader();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProv.T_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ca.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ca.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.create_example:
                return createSampleData();
            case R.id.del_all_terms:
                return deleteAllTerms();
            case R.id.create_test_alarm:
                return createTestAlarm();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean deleteAllTerms() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            
                            getContentResolver().delete(DataProv.T_URI, null, null);
                            getContentResolver().delete(DataProv.C_URI, null, null);
                            getContentResolver().delete(DataProv.C_NOTE_URI, null, null);
                            getContentResolver().delete(DataProv.ASSESS_URI, null, null);
                            getContentResolver().delete(DataProv.ASSESS_NOTE_URI, null, null);
                            restartLoader();
                            
                            Toast.makeText(TermActList.this,
                                    getString(R.string.all_terms_deld),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_del_all_terms))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

        return true;
    }

    //For testing
    private boolean createSampleData() {
        Uri term1Uri = DataMgr.insertTerm(this, "Spring 2018", "2018-01-01", "2018-05-30", 1);
        Uri term2Uri = DataMgr.insertTerm(this, "Fall 2018", "2018-07-01", "2018-11-31", 0);
        Uri term3Uri = DataMgr.insertTerm(this, "Spring 2019", "2019-01-01", "2019-05-30", 0);
        Uri term4Uri = DataMgr.insertTerm(this, "Fall 2019", "2019-07-01", "2019-11-31", 0);
        Uri term5Uri = DataMgr.insertTerm(this, "Spring 2020", "2020-01-01", "2020-05-30", 0);
        Uri term6Uri = DataMgr.insertTerm(this, "Fall 2020", "2020-07-01", "2020-11-31", 0);

        Uri course1Uri = DataMgr.insertCourse(this, Long.parseLong(term1Uri.getLastPathSegment()),
                "C196: Mobile Application Development", "2018-01-01", "2018-02-01",
                "Bruce Banner", "(801) 924-4710", "hulkSmash@wgu.edu",
                Status.IN_PROGRESS);

        DataMgr.insertCourse(this, Long.parseLong(term1Uri.getLastPathSegment()),
                "C193: Client-Server Application Development", "2018-02-01", "2018-03-01",
                "Tony Stark", "iAmIronman@wgu.edu", "(555) 124-1245",
                Status.PLAN_TO_TAKE);

        DataMgr.insertCourse(this, Long.parseLong(term1Uri.getLastPathSegment()),
                "C188: Software Engineering", "2018-03-01", "2018-04-01",
                "Peter Parker", "webSlinger@wgu.edu", "(520) 555-4545",
                Status.PLAN_TO_TAKE);

        DataMgr.insertCourseNote(this, Long.parseLong(course1Uri.getLastPathSegment()),
                "This is a short test note");

        DataMgr.insertCourseNote(this, Long.parseLong(course1Uri.getLastPathSegment()),
                getString(R.string.long_test_note));

        Uri ass1Uri = DataMgr.insertAssessment(this, Long.parseLong(course1Uri.getLastPathSegment()), "CLP1",
                "Mobile Application Development", "Here is some stuff about this class.\n" +
                        "\n" +
                        "Here is some more stuff.\n" +
                        "\n\n" +
                        "adding another line", "2018-09-02 03:45:00 PM");

        Uri ass2Uri = DataMgr.insertAssessment(this, Long.parseLong(course1Uri.getLastPathSegment()), "CAZ13",
                "Assessment 2",
                "Assessment Description", "2018-09-05 11:30:00 AM");

        DataMgr.insertAssessmentNote(this, Long.parseLong(ass1Uri.getLastPathSegment()),
                "Assessment #1 Note #1");

        DataMgr.insertAssessmentNote(this, Long.parseLong(ass1Uri.getLastPathSegment()),
                "Assessment #1 Note #2");

        DataMgr.insertAssessmentNote(this, Long.parseLong(ass2Uri.getLastPathSegment()),
                "Assessment #2 Note #1");

        DataMgr.insertAssessmentNote(this, Long.parseLong(ass2Uri.getLastPathSegment()),
                "Assessment #2 Note #2");

        DataMgr.insertAssessmentNote(this, Long.parseLong(ass2Uri.getLastPathSegment()),
                "Assessment #2 Note #3");


        restartLoader();
        return true;
    }

    private boolean createTestAlarm() {
        Long time = new GregorianCalendar().getTimeInMillis() + 5000;

        Intent intentAlarm = new Intent(this, Alarm.class);
        intentAlarm.putExtra("text", "Test notification!!!");
        intentAlarm.putExtra("title", "TEST Notification!!!!");

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_ONE_SHOT));
        Toast.makeText(this, "Alarm scheduled 10 second from now", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public void openNewTermEditor(View view) {
        Intent intent = new Intent(this, TermActEditor.class);
        startActivityForResult(intent, T_ACT_EDITOR_CODE);
    }
}
