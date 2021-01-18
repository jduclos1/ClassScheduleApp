package com.example.jduclos1.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class CourseNoteActViewer extends AppCompatActivity {
    private static final int COURSE_NOTE_ACT_EDITOR_CODE = 11111;
    private Uri nUri;
    private TextView tvNoteText;
    private long cNoteId;
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_c_note_viewer);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNoteText = (TextView) findViewById(R.id.tvCourseNoteText);

        nUri = getIntent().getParcelableExtra(DataProv.C_NOTE_CONTENT_TYPE);
        if (nUri != null) {
            cNoteId = Long.parseLong(nUri.getLastPathSegment());
            setTitle("View Course Note");
            loadNote();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadNote();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.del_course_note:
                return deleteCourseNote();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_course_note_viewer, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.share_menu_item);

        // Fetch and store ShareActionProvider
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        CourseNote courseNote = DataMgr.getCourseNote(this, cNoteId);
        Course course = DataMgr.getCourse(this, courseNote.cId);


        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject = course.cName + ": Course Note";
        String shareBody = courseNote.text;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        //then set the sharingIntent
        shareActionProvider.setShareIntent(sharingIntent);
        return true;
    }

    private void loadNote() {
        CourseNote cn = DataMgr.getCourseNote(this, cNoteId);
        tvNoteText.setText(cn.text);
        tvNoteText.setMovementMethod(new ScrollingMovementMethod());
    }

    private boolean deleteCourseNote() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    // Confirm that user wishes to proceed
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            DataMgr.deleteCourseNote(CourseNoteActViewer.this, cNoteId);
                            setResult(RESULT_OK);
                            finish();

                            // Notify that delete was completed
                            Toast.makeText(CourseNoteActViewer.this,
                                    R.string.note_deld,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_del_note)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

        return true;
    }

    public void handleEditNote(View view) {
        Intent intent = new Intent(this, CourseNoteActEditor.class);
        intent.putExtra(DataProv.C_NOTE_CONTENT_TYPE, nUri);
        startActivityForResult(intent, COURSE_NOTE_ACT_EDITOR_CODE);
    }
}
