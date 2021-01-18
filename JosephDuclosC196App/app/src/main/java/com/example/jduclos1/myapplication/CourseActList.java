package com.example.jduclos1.myapplication;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class CourseActList extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int C_ACT_VIEWER_CODE = 11111;
    private static final int C_ACT_EDITOR_CODE = 22222;
    private long tId;
    private Uri tUri;
    private Term term;
    private MySimpleCursorAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_c_list);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseActList.this, CourseActEditor.class);
                intent.putExtra(DataProv.T_CONTENT_TYPE, tUri);
                startActivityForResult(intent, C_ACT_EDITOR_CODE);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        tUri = intent.getParcelableExtra(DataProv.T_CONTENT_TYPE);
        loadTermData();
        bindClassList();

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProv.C_URI, DatabaseMgr.C_COLS, DatabaseMgr.C_T_ID + " = " + this.tId,
                null, null);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadTermData();
        restartLoader();
    }

    private void loadTermData() {
        if (tUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        }

        else {
            tId = Long.parseLong(tUri.getLastPathSegment());
            term = DataMgr.getTerm(this, tId);
            setTitle("Courses");
        }
    }

    private void bindClassList() {
        String[] from = { DatabaseMgr.C_NAME, DatabaseMgr.C_START, DatabaseMgr.C_END, DatabaseMgr.C_STATUS };
        int[] to = { R.id.tvCourseName, R.id.tvCourseStartDate, R.id.tvCourseEndDate, R.id.tvCourseStatus };

        ca = new MySimpleCursorAdapter(this, R.layout.c_list_item, null, from, to, 0);
        DataProv db = new DataProv();

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(ca);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseActList.this, CourseActViewer.class);
                Uri uri = Uri.parse(DataProv.C_URI + "/" + id);
                intent.putExtra(DataProv.C_CONTENT_TYPE, uri);
                startActivityForResult(intent, C_ACT_VIEWER_CODE);
            }
        });
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public class MySimpleCursorAdapter extends SimpleCursorAdapter {

        public MySimpleCursorAdapter(Context context, int layout, Cursor c,
                                     String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, 0);
        }

        @Override
        public void setViewText(TextView v, String text) {

            if (v.getId() == R.id.tvCourseStatus) {
                String status = "";

                switch(text) {
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

                v.setText("Status: " + status);
            }

            else {
                v.setText(text);
            }
        }
    }
}
