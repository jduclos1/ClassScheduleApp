package com.example.jduclos1.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class AssessmentNoteActViewer extends AppCompatActivity {

    private static final int ASSESS_NOTE_ACT_EDITOR_CODE = 11111;

    private Uri assessNoteUri;
    private TextView tvAssessmentNoteText;
    private long assessNoteId;

    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_assess_note_viewer);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvAssessmentNoteText = (TextView) findViewById(R.id.tvAssessmentNoteText);

        assessNoteUri = getIntent().getParcelableExtra(DataProv.ASSESS_NOTE_CONTENT_TYPE);
        if (assessNoteUri != null) {
            assessNoteId = Long.parseLong(assessNoteUri.getLastPathSegment());
            setTitle("View Assessment Note");
            loadNote();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_assessment_note_viewer, menu);
        MenuItem item = menu.findItem(R.id.share_menu_item);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        AssessmentNote assessmentNote = DataMgr.getAssessmentNote(this, assessNoteId);
        Assessment assessment= DataMgr.getAssessment(this, assessmentNote.assessId);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject = assessment.code + " " + assessment.name + ": Assessment Note";
        String shareBody = assessmentNote.text;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        shareActionProvider.setShareIntent(sharingIntent);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            loadNote();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.del_assess_note:
                return deleteAssessmentNote();
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void loadNote() {
        AssessmentNote cn = DataMgr.getAssessmentNote(this, assessNoteId);
        tvAssessmentNoteText.setText(cn.text);
        tvAssessmentNoteText.setMovementMethod(new ScrollingMovementMethod());
    }

    private boolean deleteAssessmentNote() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    // Confirm that user wishes to proceed
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            DataMgr.deleteAssessmentNote(AssessmentNoteActViewer.this, assessNoteId);
                            setResult(RESULT_OK);
                            finish();

                            // Notify that delete was completed
                            Toast.makeText(AssessmentNoteActViewer.this,
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
        Intent intent = new Intent(this, AssessmentNoteActEditor.class);
        intent.putExtra(DataProv.ASSESS_NOTE_CONTENT_TYPE, assessNoteUri);
        startActivityForResult(intent, ASSESS_NOTE_ACT_EDITOR_CODE);
    }
}
