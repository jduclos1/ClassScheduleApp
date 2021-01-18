package com.example.jduclos1.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class TermActViewer extends AppCompatActivity {

    private static final int T_ACT_EDITOR_CODE = 11111;
    private static final int C_ACT_LIST_CODE = 33333;
    private Uri tUri;
    private Term term;
    private CursorAdapter ca;
    private TextView tv_title;
    private TextView tv_start;
    private TextView tv_end;
    private Menu menu;
    private long tId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_t_viewer);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        tUri = intent.getParcelableExtra(DataProv.T_CONTENT_TYPE);

        findElements();
        loadTermData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_term_viewer, menu);
        this.menu = menu;
        showAppropriateMenuOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.activate_term:
                return markTermActive();
            case R.id.edit_term:
                Intent intent = new Intent(this, TermActEditor.class);
                Uri uri = Uri.parse(DataProv.T_URI + "/" + term.tId);
                intent.putExtra(DataProv.T_CONTENT_TYPE, uri);
                startActivityForResult(intent, T_ACT_EDITOR_CODE);
                break;
            case R.id.del_term:
                return deleteTerm();
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void loadTermData() {
        if (tUri == null) {
            setResult(RESULT_CANCELED);
            finish();
        }

        else {
            tId = Long.parseLong(tUri.getLastPathSegment());
            term = DataMgr.getTerm(this, tId);

            setTitle("View Term");
            tv_title.setText(term.tName);
            tv_start.setText(term.tStart);
            tv_end.setText(term.tEnd);
        }
    }

    private void findElements() {
        tv_title = (TextView) findViewById(R.id.tvTermViewTermTitle);
        tv_start = (TextView) findViewById(R.id.tvTermViewStartDate);
        tv_end = (TextView) findViewById(R.id.tvTermViewEndDate);
    }

    private boolean markTermActive() {
        Cursor termCursor = getContentResolver().query(DataProv.T_URI, null, null, null, null);
        ArrayList<Term> termList = new ArrayList<>();
        while  (termCursor.moveToNext()) {
            termList.add(DataMgr.getTerm(this, termCursor.getLong(termCursor.getColumnIndex(DatabaseMgr.T_TABLE_ID))));
        }

        for(Term term : termList) {
            term.deactivate(this);
        }

        this.term.activate(this);
        showAppropriateMenuOptions();

        Toast.makeText(TermActViewer.this, R.string.term_marked_active, Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean deleteTerm() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    // Confirm
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            long classCount = term.getClassCount(TermActViewer.this);
                            if (classCount == 0) {
                                getContentResolver().delete(DataProv.T_URI, DatabaseMgr.T_TABLE_ID + " = " + tId, null);

                                // Notify delete was completed
                                Toast.makeText(TermActViewer.this,
                                        getString(R.string.term_deld),
                                        Toast.LENGTH_SHORT).show();

                                setResult(RESULT_OK);
                                finish();
                            }
                            else {
                                Toast.makeText(TermActViewer.this,
                                        getString(R.string.too_many),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_del_term))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

        return true;
    }

    public void openClassList(View view) {
        Intent intent = new Intent(this, CourseActList.class);
        intent.putExtra(DataProv.T_CONTENT_TYPE, tUri);
        startActivityForResult(intent, C_ACT_LIST_CODE);
    }

    private void showAppropriateMenuOptions() {
        if (term.tStatus == 1) {
            menu.findItem(R.id.activate_term).setVisible(false);
        }

    }
}
