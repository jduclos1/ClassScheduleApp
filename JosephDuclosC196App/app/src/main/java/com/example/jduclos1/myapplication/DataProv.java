package com.example.jduclos1.myapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class DataProv extends ContentProvider {

    private static final String AUTH = "com.example.jduclos1.myapplication.dataprov";
    private static final String T_PATH = "terms";
    public static final String C_PATH = "courses";
    public static final String C_NOTE_PATH = "courseNotes";
    public static final String ASSESS_PATH = "assessments";
    public static final String ASSESS_NOTE_PATH = "assessmentNotes";
    public static final Uri T_URI = Uri.parse("content://" + AUTH + "/" + T_PATH);
    public static final Uri C_URI = Uri.parse("content://" + AUTH + "/" + C_PATH);
    public static final Uri C_NOTE_URI = Uri.parse("content://" + AUTH + "/" + C_NOTE_PATH);
    public static final Uri ASSESS_URI = Uri.parse("content://" + AUTH + "/" + ASSESS_PATH);
    public static final Uri ASSESS_NOTE_URI = Uri.parse("content://" + AUTH + "/" + ASSESS_NOTE_PATH);

    // Constant to identify the requested operation
    private static final int TERMS = 1;
    public static final int TERMS_ID = 2;
    public static final int COURSES = 3;
    public static final int C_ID = 4;
    public static final int C_NOTES = 5;
    public static final int C_NOTES_ID = 6;
    public static final int ASSESSS = 7;
    public static final int ASSESS_ID = 8;
    public static final int ASSESS_NOTES = 9;
    public static final int ASSESS_NOTES_ID = 10;
    public static final String T_CONTENT_TYPE = "term";
    public static final String C_CONTENT_TYPE = "course";
    public static final String C_NOTE_CONTENT_TYPE = "courseNote";
    public static final String ASSESS_CONTENT_TYPE = "assessment";
    public static final String ASSESS_NOTE_CONTENT_TYPE = "assessmentNote";

    private SQLiteDatabase db;
    private String currentTable;
    

    public static final UriMatcher mUri = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mUri.addURI(AUTH, T_PATH, TERMS);
        mUri.addURI(AUTH, T_PATH + "/#", TERMS_ID);
        mUri.addURI(AUTH, C_PATH, COURSES);
        mUri.addURI(AUTH, C_PATH + "/#", C_ID);
        mUri.addURI(AUTH, C_NOTE_PATH, C_NOTES);
        mUri.addURI(AUTH, C_NOTE_PATH + "/#", C_NOTES_ID);
        mUri.addURI(AUTH, ASSESS_PATH, ASSESSS);
        mUri.addURI(AUTH, ASSESS_PATH + "/#", ASSESS_ID);
        mUri.addURI(AUTH, ASSESS_NOTE_PATH, ASSESS_NOTES);
        mUri.addURI(AUTH, ASSESS_NOTE_PATH + "/#", ASSESS_NOTES_ID);

    }
    
    @Override
    public boolean onCreate() {
        DatabaseMgr helper = new DatabaseMgr(getContext());
        db = helper.getWritableDatabase();
        return true;
    }

    @Override
    public String getType(Uri mUri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] proj, String s, String[] sArgs, String sOrder) {
        switch (mUri.match(uri)) {
            case TERMS:
                return db.query(DatabaseMgr.TERMS_TABLE, DatabaseMgr.T_COLS, s, null, null,
                        null, DatabaseMgr.T_TABLE_ID + " ASC");
            case COURSES:
                return db.query(DatabaseMgr.COURSES_TABLE, DatabaseMgr.C_COLS, s, null, null,
                        null, DatabaseMgr.C_TABLE_ID + " ASC");
            case C_ID:
                return db.query(DatabaseMgr.COURSES_TABLE, DatabaseMgr.C_COLS, DatabaseMgr.C_TABLE_ID + "=" + uri.getLastPathSegment(),
                        null, null, null, DatabaseMgr.C_TABLE_ID + " ASC" );
            case C_NOTES:
                return db.query(DatabaseMgr.C_NOTES_TABLE, DatabaseMgr.C_NOTE_COLS, s, null, null,
                        null, DatabaseMgr.C_NOTE_TABLE_ID + " ASC");
            case ASSESSS:
                return db.query(DatabaseMgr.ASSESS_TABLE, DatabaseMgr.ASSESS_COLS, s, null, null,
                        null, DatabaseMgr.ASSESS_TABLE_ID + " ASC");
            case ASSESS_NOTES:
                return db.query(DatabaseMgr.ASSESS_NOTES_TABLE, DatabaseMgr.ASSESS_NOTE_COLS, s, null, null,
                        null, DatabaseMgr.ASSESS_NOTE_TABLE_ID + " ASC");
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + mUri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues v) {
        long id;
        switch (mUri.match(uri)) {
            case TERMS:
                id = db.insert(DatabaseMgr.TERMS_TABLE, null, v);
                Log.d("DataProvider", "Inserted _Term: " + id);
                return uri.parse(T_PATH + "/" + id);
            case COURSES:
                id = db.insert(DatabaseMgr.COURSES_TABLE, null, v);
                Log.d("DataProvider", "Inserted _Term: " + id);
                return uri.parse(C_PATH + "/" + id);
            case C_NOTES:
                id = db.insert(DatabaseMgr.C_NOTES_TABLE, null, v);
                Log.d("DataProvider", "Inserted _CourseNote: " + id);
                return uri.parse(C_NOTE_PATH + "/" + id);
            case ASSESSS:
                id = db.insert(DatabaseMgr.ASSESS_TABLE, null, v);
                Log.d("DataProvider", "Inserted _Assessment: " + id);
                return uri.parse(ASSESS_PATH + "/" + id);
            case ASSESS_NOTES:
                id = db.insert(DatabaseMgr.ASSESS_NOTES_TABLE, null, v);
                Log.d("DataProvider", "Inserted _AssessmentNote: " + id);
                return uri.parse(ASSESS_NOTE_PATH + "/" + id);

            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] sArgs) {
        switch (mUri.match(uri)) {
            case TERMS:
                return db.delete(DatabaseMgr.TERMS_TABLE, s, sArgs);
            case COURSES:
                return db.delete(DatabaseMgr.COURSES_TABLE, s, sArgs);
            case C_NOTES:
                return db.delete(DatabaseMgr.C_NOTES_TABLE, s, sArgs);
            case ASSESSS:
                return db.delete(DatabaseMgr.ASSESS_TABLE, s, sArgs);
            case ASSESS_NOTES:
                return db.delete(DatabaseMgr.ASSESS_NOTES_TABLE, s, sArgs);
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues v, String s, String[] sArgs) {
        switch (mUri.match(uri)) {
            case TERMS:
                return db.update(DatabaseMgr.TERMS_TABLE, v, s, sArgs);
            case COURSES:
                return db.update(DatabaseMgr.COURSES_TABLE, v, s, sArgs);
            case C_NOTES:
                return db.update(DatabaseMgr.C_NOTES_TABLE, v, s, sArgs);
            case ASSESSS:
                return db.update(DatabaseMgr.ASSESS_TABLE, v, s, sArgs);
            case ASSESS_NOTES:
                return db.update(DatabaseMgr.ASSESS_NOTES_TABLE, v, s, sArgs);
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
    }
}
