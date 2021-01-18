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

public class CourseNoteActList extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int C_NOTE_ACT_EDITOR_CODE = 11111;
    private static final int C_NOTE_ACT_VIEWER_CODE = 22222;
    private Uri cUri;
    private long cId;

    private CursorAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_c_note_list);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cUri = getIntent().getParcelableExtra(DataProv.C_CONTENT_TYPE);
        cId = Long.parseLong(cUri.getLastPathSegment());

        bindCourseNoteList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseNoteActList.this, CourseNoteActEditor.class);
                intent.putExtra(DataProv.C_CONTENT_TYPE, cUri);
                startActivityForResult(intent, C_NOTE_ACT_EDITOR_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProv.C_NOTE_URI, DatabaseMgr.C_NOTE_COLS,
                DatabaseMgr.C_NOTE_C_ID + " = " + this.cId, null, null);
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

    private void bindCourseNoteList() {
        String[] from = { DatabaseMgr.C_NOTE_TEXT };
        int[] to = { R.id.tvCourseNoteText };

        ca = new SimpleCursorAdapter(this, R.layout.c_note_list_item, null, from, to, 0);
        DataProv db = new DataProv();

        ListView list = (ListView) findViewById(R.id.courseNoteListView);
        list.setAdapter(ca);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseNoteActList.this, CourseNoteActViewer.class);
                Uri uri = Uri.parse(DataProv.C_NOTE_URI + "/" + id);
                intent.putExtra(DataProv.C_NOTE_CONTENT_TYPE, uri);
                startActivityForResult(intent, C_NOTE_ACT_VIEWER_CODE);
            }
        });
    }
    
    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
