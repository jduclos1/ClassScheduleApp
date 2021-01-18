package com.example.jduclos1.myapplication;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class AssessmentActList extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ASSESS_ACTIVITY_VIEWER_CODE = 11111;
    private static final int ASSESS_ACTIVITY_EDITOR_CODE = 22222;
    private CursorAdapter ca;

    private long cId;
    private Uri cUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_assess_list);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cUri = getIntent().getParcelableExtra(DataProv.C_CONTENT_TYPE);
        cId = Long.parseLong(cUri.getLastPathSegment());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentActList.this, AssessmentActEditor.class);
                intent.putExtra(DataProv.C_CONTENT_TYPE, cUri);
                startActivityForResult(intent, ASSESS_ACTIVITY_EDITOR_CODE);
            }
        });

        bindAssessmentList();
        getLoaderManager().initLoader(0, null, this);

    }

    private void bindAssessmentList() {
        String[] from = { DatabaseMgr.ASSESS_CODE, DatabaseMgr.ASSESS_NAME, DatabaseMgr.ASSESS_DATE_TIME };
        int[] to = { R.id.tvAssessmentCode, R.id.tvAssessmentName, R.id.tvAssessmentDatetime };

        ca = new SimpleCursorAdapter(this, R.layout.assess_list_item, null, from, to, 0);
        DataProv db = new DataProv();

        ListView list = (ListView) findViewById(R.id.assessmentListView);
        list.setAdapter(ca);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssessmentActList.this, AssessmentActViewer.class);
                Uri uri = Uri.parse(DataProv.ASSESS_URI + "/" + id);
                intent.putExtra(DataProv.ASSESS_CONTENT_TYPE, uri);
                startActivityForResult(intent, ASSESS_ACTIVITY_VIEWER_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProv.ASSESS_URI, DatabaseMgr.ASSESS_COLS,
                DatabaseMgr.ASSESS_C_ID + " = " + this.cId, null, null);
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
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
