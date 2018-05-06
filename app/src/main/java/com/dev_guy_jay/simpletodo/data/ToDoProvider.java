package com.dev_guy_jay.simpletodo.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dev_guy_jay.simpletodo.R;

import java.io.IOException;

import static com.dev_guy_jay.simpletodo.Utils.Utils.isDateValid;
import static com.dev_guy_jay.simpletodo.data.ToDoDBHelper.ALARM_OFF;
import static com.dev_guy_jay.simpletodo.data.ToDoDBHelper.ALARM_ON;

public class ToDoProvider extends ContentProvider {
    public final String  ProviderLog = getClass().getCanonicalName();

    ToDoDBHelper mToDoHelper;

    public static final int TODOS = 100;
    public static final int TODO_ID = 101;

    public static final String CONTENT_AUTHORITY = "com.dev_guy_jay.simpletodo";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TODOS = "simpletodo";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TODOS);

    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODOS;

    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODOS;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TODOS, TODOS);

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TODOS + "/#", TODO_ID);
    }

    @Override
    public boolean onCreate() {
        mToDoHelper = new ToDoDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) throws IllegalArgumentException {
        SQLiteDatabase database = mToDoHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOS:

                cursor = database.query(ToDoDBHelper.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TODO_ID:

                selection = ToDoDBHelper._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(ToDoDBHelper.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(R.string.error_unknown_uri + " " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) throws IllegalArgumentException{
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOS:
                return CONTENT_LIST_TYPE;
            case TODO_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("URI Not Known " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) throws IllegalArgumentException {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOS:
                getContext().getContentResolver().notifyChange(uri, null);
                try{
                    return insertTodo(uri, values);
                }catch(IOException e){
                    return null;
                }

            default:
                throw new IllegalArgumentException("Cannot Insert for " + uri);
        }
    }

    private Uri insertTodo(Uri uri, ContentValues values)throws IOException{
        String name = values.getAsString(ToDoDBHelper.COLUMN_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("Title is required");
        }

        Integer alarm = values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_TURN_ON);
        if (alarm == null || !isAlarmValid(alarm)) {
            throw new IllegalArgumentException("Error problem with Alarm");
        }
        if (alarm == ALARM_ON){
            if(isDateValid(values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_DATE_YEAR), values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_DATE_MONTH), values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_DATE_DAY), values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_TIME_HOUR), values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_TIME_MINUTE))) {

            }else{
                throw new IOException("Cannot set alarm before current time");
            }
        }

        SQLiteDatabase database = mToDoHelper.getWritableDatabase();

        long id = database.insert(ToDoDBHelper.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(ProviderLog, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) throws IllegalArgumentException{
        SQLiteDatabase database = mToDoHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOS:
                rowsDeleted = database.delete(ToDoDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ID:
                selection = ToDoDBHelper._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ToDoDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot Delete for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOS:
                try{
                    return updateToDo(uri, values, selection, selectionArgs);
                }catch(IOException e){
                    return -1;
                }
            case TODO_ID:
                selection = ToDoDBHelper._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                try{
                    return updateToDo(uri, values, selection, selectionArgs);
                }catch(IOException e){
                    return -1;
                }
            default:
                throw new IllegalArgumentException("Cannot update for " + uri);
        }
    }

    private int updateToDo(Uri uri, ContentValues values, String selection, String[] selectionArgs) throws IOException{
        if (values.containsKey(ToDoDBHelper.COLUMN_TITLE)) {
            String name = values.getAsString(ToDoDBHelper.COLUMN_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("Title is Required");
            }
        }

        Integer alarm = values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_TURN_ON);
        if (alarm == null || !isAlarmValid(alarm)) {
            throw new IllegalArgumentException("Error problem with Alarm");
        }

        if(alarm == ALARM_ON){
            if(isDateValid(values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_DATE_YEAR), values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_DATE_MONTH), values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_DATE_DAY), values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_TIME_HOUR), values.getAsInteger(ToDoDBHelper.COLUMN_REMAINER_TIME_MINUTE))) {

            }else{
                throw new IOException("Cannot set alarm before current time");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mToDoHelper.getWritableDatabase();

        int rowsUpdated = database.update(ToDoDBHelper.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    public static boolean isAlarmValid(int alarm) {
        if (alarm == ALARM_OFF || alarm == ALARM_ON) {
            return true;
        }
        return false;
    }
}
