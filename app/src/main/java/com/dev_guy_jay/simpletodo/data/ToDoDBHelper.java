package com.dev_guy_jay.simpletodo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ToDoDBHelper extends SQLiteOpenHelper implements BaseColumns {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "todo.db";
    public static final String TABLE_NAME = "todo";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_DATE_TIME_CREATED = "date_time_created";
    public static final String COLUMN_REMAINER_TURN_ON = "remainer_turn_on";
    public static final String COLUMN_REMAINER_DATE_YEAR = "reminder_year";
    public static final String COLUMN_REMAINER_DATE_MONTH = "reminder_month";
    public static final String COLUMN_REMAINER_DATE_DAY = "reminder_day";
    public static final String COLUMN_REMAINER_TIME_HOUR = "reminder_hour";
    public static final String COLUMN_REMAINER_TIME_MINUTE = "reminder_minute";
    public static final String COLUMN_NOTIFICATION_ID = "notification_id";

    public static final int ALARM_OFF = 0;
    public static final int ALARM_ON = 1;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME  + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_CONTENT + " TEXT,"
            + COLUMN_DATE_TIME_CREATED + " TEXT NOT NULL,"
            + COLUMN_REMAINER_TURN_ON + " INTEGER NOT NULL,"
            + COLUMN_REMAINER_DATE_YEAR + " INTEGER,"
            + COLUMN_REMAINER_DATE_MONTH + " INTEGER,"
            + COLUMN_REMAINER_DATE_DAY + " INTEGER,"
            + COLUMN_REMAINER_TIME_HOUR + " INTEGER, "
            + COLUMN_REMAINER_TIME_MINUTE + " INTEGER, "
            + COLUMN_NOTIFICATION_ID + " INTEGER NOT NULL"
            + ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public ToDoDBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
