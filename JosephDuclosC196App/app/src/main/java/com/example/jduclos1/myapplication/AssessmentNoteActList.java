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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class AssessmentNoteActList extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ASSESS_NOTE_ACT_EDITOR_CODE = 11111;
    private static final int ASSESS_NOTE_ACT_VIEWER_CODE = 22222;

    private Uri assessmentUri;
    private long assessmentId;

    private android.widget.CursorAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_assess_note_list);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assessmentUri = getIntent().getParcelableExtra(DataProv.ASSESS_CONTENT_TYPE);
        assessmentId = Long.parseLong(assessmentUri.getLastPathSegment());

        bindAssessmentNoteList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentNoteActList.this, AssessmentNoteActEditor.class);
                intent.putExtra(DataProv.ASSESS_CONTENT_TYPE, assessmentUri);
                startActivityForResult(intent, ASSESS_NOTE_ACT_EDITOR_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProv.ASSESS_NOTE_URI, DatabaseMgr.ASSESS_NOTE_COLS,
                DatabaseMgr.ASSESS_NOTE_ASSESS_ID + " = " + this.assessmentId, null, null);
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

    private void bindAssessmentNoteList() {
        String[] from = { DatabaseMgr.ASSESS_NOTE_TEXT };
        int[] to = { R.id.tvAssessmentNoteText };

        ca = new SimpleCursorAdapter(this, R.layout.assess_note_list_item, null, from, to, 0);
        DataProv db = new DataProv();

        ListView list = (ListView) findViewById(R.id.assessmentNoteListView);
        list.setAdapter(ca);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssessmentNoteActList.this, AssessmentNoteActViewer.class);
                Uri uri = Uri.parse(DataProv.ASSESS_NOTE_URI + "/" + id);
                intent.putExtra(DataProv.ASSESS_NOTE_CONTENT_TYPE, uri);
                startActivityForResult(intent, ASSESS_NOTE_ACT_VIEWER_CODE);
            }
        });
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
