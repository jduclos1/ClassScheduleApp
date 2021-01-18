package com.example.jduclos1.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseMgr extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "courses.db";
    private static final int DATABASE_VERSION = 11;

    //Constants for identifying table and columns

    // _Term Table
    public static final String TERMS_TABLE = "terms";
    public static final String T_TABLE_ID = "_id";
    public static final String T_NAME = "name";
    public static final String T_START = "startdate";
    public static final String T_END = "enddate";
    public static final String T_ACTIVE = "active";
    public static final String T_CREATED = "_created";

    // Class Table
    public static final String COURSES_TABLE = "courses";
    public static final String C_T_ID = "termId";
    public static final String C_TABLE_ID = "_id";
    public static final String C_NAME = "name";
    public static final String C_START = "startdate";
    public static final String C_END = "enddate";
    public static final String C_CREATED = "_created";
    public static final String C_DESC = "description";
    public static final String C_MENTOR = "mentor";
    public static final String C_MENTOR_PHONE = "mentorPhone";
    public static final String C_MENTOR_EMAIL = "mentorEmail";
    public static final String C_STATUS = "status";
    public static final String C_NOTIFICATIONS = "notifications";

    // course Note Table
    public static final String C_NOTES_TABLE = "course_notes";
    public static final String C_NOTE_TABLE_ID = "_id";
    public static final String C_NOTE_C_ID = "courseId";
    public static final String C_NOTE_TEXT = "text";
    public static final String C_NOTE_ATTACHMENT_URI = "uri";
    public static final String C_NOTE_CREATED = "_created";

    public static final String ASSESS_TABLE = "assessments";
    public static final String ASSESS_TABLE_ID = "_id";
    public static final String ASSESS_C_ID = "courseId";
    public static final String ASSESS_CODE = "code";
    public static final String ASSESS_NAME = "name";
    public static final String ASSESS_DATE_TIME = "dateTime";
    public static final String ASSESS_DESC = "description";
    public static final String ASSESS_CREATED = "_created";
    public static final String ASSESS_NOTIFICATIONS = "notifications";

    public static final String ASSESS_NOTES_TABLE = "assessment_notes";
    public static final String ASSESS_NOTE_TABLE_ID = "_id";
    public static final String ASSESS_NOTE_ASSESS_ID = "assessmentId";
    public static final String ASSESS_NOTE_TEXT = "text";
    public static final String ASSESS_NOTE_ATTACHMENT_URI = "uri";
    public static final String ASSESS_NOTE_CREATED = "_created";

    public static final String[] T_COLS = {T_TABLE_ID, T_NAME, T_START,
            T_END, T_ACTIVE, T_CREATED};

    public static final String[] C_COLS = {C_TABLE_ID, C_NAME, C_START,
            C_END, C_CREATED, C_DESC, C_MENTOR, C_MENTOR_EMAIL,
            C_MENTOR_PHONE, C_NOTIFICATIONS, C_STATUS};

    public static final String[] C_NOTE_COLS = {C_NOTE_TABLE_ID, C_NOTE_C_ID,
            C_NOTE_TEXT, C_NOTE_ATTACHMENT_URI};

    public static final String[] ASSESS_COLS = {ASSESS_TABLE_ID, ASSESS_C_ID,
            ASSESS_CODE, ASSESS_NAME, ASSESS_DESC, ASSESS_DATE_TIME,
            ASSESS_NOTIFICATIONS};

    public static final String[] ASSESS_NOTE_COLS = {ASSESS_NOTE_TABLE_ID,
            ASSESS_NOTE_ASSESS_ID, ASSESS_NOTE_TEXT, ASSESS_NOTE_ATTACHMENT_URI};


    public static final String C_NOTE_TABLE_CREATE =
            "CREATE TABLE " + C_NOTES_TABLE + " (" +
                    C_NOTE_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_NOTE_C_ID + " INTEGER, " +
                    C_NOTE_TEXT + " TEXT, " +
                    C_NOTE_ATTACHMENT_URI + " TEXT, " +
                    C_NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + C_NOTE_C_ID + ") REFERENCES " + COURSES_TABLE + "(" + C_TABLE_ID + ")" +
                    ")";

    public static final String ASSESS_TABLE_CREATE =
            "CREATE TABLE " + ASSESS_TABLE + " (" +
                    ASSESS_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESS_C_ID + " INTEGER, " +
                    ASSESS_CODE + " TEXT, " +
                    ASSESS_NAME + " TEXT, " +
                    ASSESS_DESC + " TEXT, " +
                    ASSESS_DATE_TIME + " TEXT, " +
                    ASSESS_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                    ASSESS_NOTIFICATIONS + " INTEGER, " +
                    "FOREIGN KEY(" + ASSESS_C_ID + ") REFERENCES " + COURSES_TABLE + "(" + C_TABLE_ID + ")" +
                    ")";

    public static final String ASSESS_NOTE_TABLE_CREATE =
            "CREATE TABLE " + ASSESS_NOTES_TABLE + " (" +
                    ASSESS_NOTE_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESS_NOTE_ASSESS_ID + " INTEGER, " +
                    ASSESS_NOTE_TEXT + " TEXT, " +
                    ASSESS_NOTE_ATTACHMENT_URI + " TEXT, " +
                    ASSESS_NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + ASSESS_NOTE_ASSESS_ID + ") REFERENCES " + ASSESS_TABLE + "(" + ASSESS_TABLE_ID + ")" +
                    ")";


    //SQL to create term table
    private static final String T_TABLE_CREATE =
            "CREATE TABLE " + TERMS_TABLE + " (" +
                    T_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    T_NAME + " TEXT, " +
                    T_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                    T_START + " DATE, " +
                    T_END + " DATE, " +
                    T_ACTIVE + " INTEGER" +
                    ")";

    // SQL to create course table
    private static final String C_TABLE_CREATE =
            "CREATE TABLE " + COURSES_TABLE + " (" +
                    C_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_T_ID + " INTEGER, " +
                    C_NAME + " TEXT, " +
                    C_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                    C_START + " DATE, " +
                    C_END + " DATE, " +
                    C_DESC + " TEXT, " +
                    C_MENTOR + " TEXT, " +
                    C_MENTOR_EMAIL + " TEXT, " +
                    C_MENTOR_PHONE + " TEXT, " +
                    C_STATUS + " TEXT, " +
                    C_NOTIFICATIONS + " INTEGER, " +
                    "FOREIGN KEY(" + C_T_ID + ") REFERENCES " + TERMS_TABLE + "(" + T_TABLE_ID + ")" +
                    ")";



    public DatabaseMgr(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(T_TABLE_CREATE);
        db.execSQL(C_TABLE_CREATE);
        db.execSQL(C_NOTE_TABLE_CREATE);
        db.execSQL(ASSESS_TABLE_CREATE);
        db.execSQL(ASSESS_NOTE_TABLE_CREATE); }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ASSESS_NOTES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ASSESS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + C_NOTES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COURSES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TERMS_TABLE);
        onCreate(db);
    }
}