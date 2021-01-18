package com.example.jduclos1.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;


public class Main extends AppCompatActivity {


    private static final int T_ACT_LIST_CODE = 11111;
    private static final int T_ACT_VIEWER_CODE = 22222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);

    }

    public void openTermList(View view) {
        Intent intent = new Intent(this, TermActList.class);
        startActivityForResult(intent, T_ACT_LIST_CODE);
    }

    public void openCurrentTerm(View view) {
        Cursor c = getContentResolver().query(DataProv.T_URI, null, DatabaseMgr.T_ACTIVE + " =1", null, null);
        while (c.moveToNext()) {
            Intent intent = new Intent(this, TermActViewer.class);
            long id = c.getLong(c.getColumnIndex(DatabaseMgr.T_TABLE_ID));
            Uri uri = Uri.parse(DataProv.T_URI + "/" + id);
            intent.putExtra(DataProv.T_CONTENT_TYPE, uri);
            startActivityForResult(intent, T_ACT_VIEWER_CODE);
            return;
        }

        Toast.makeText(this,
                "You have no active terms marked. Activate a term.",
                Toast.LENGTH_SHORT).show();
    }
}
