package com.dev_guy_jay.simpletodo.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.dev_guy_jay.simpletodo.R;
import com.dev_guy_jay.simpletodo.Utils.AlarmUtils;
import com.dev_guy_jay.simpletodo.Utils.Utils;
import com.dev_guy_jay.simpletodo.data.ToDoDBHelper;
import com.dev_guy_jay.simpletodo.data.ToDoProvider;

import java.util.Calendar;
import static com.dev_guy_jay.simpletodo.Utils.Utils.getDateTime;
import static com.dev_guy_jay.simpletodo.data.ToDoDBHelper.ALARM_OFF;
import static com.dev_guy_jay.simpletodo.data.ToDoDBHelper.ALARM_ON;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TODO_LOADER = 0;

    AlarmUtils alarmUtils;

    private Uri mToDoUri;

    RelativeLayout dateAndTimeLayout;
    ToggleButton toggleButton;
    EditText editTitle;
    EditText editDescription;
    EditText editDate;
    EditText editTime;

    private int storeNotificationId;

    private int mYear = 9999;
    private int mMonth = 12;
    private int mDay = 1;
    private int mHour = Calendar.HOUR_OF_DAY + 1;
    private int mMinute = 0;
    private long mLastClickTime = 0;
    private int mAlarm = ALARM_OFF;
    private boolean mAlarmWasOn = false;

    private boolean hasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            hasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.editor);

        Intent intent = getIntent();
        mToDoUri= intent.getData();

        dateAndTimeLayout = (RelativeLayout) findViewById(R.id.date_and_time_layout);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setText(R.string.alarm_off);

        if (mToDoUri == null) {
            setTitle(R.string.add_todo);
        } else {
            setTitle(R.string.edit_todo);
            getLoaderManager().initLoader(TODO_LOADER, null, this);
        }

        alarmUtils = new AlarmUtils(getApplicationContext());

        editTitle = (EditText) findViewById(R.id.title);
        editDescription = (EditText) findViewById(R.id.content);
        editTime=(EditText)findViewById(R.id.time_text);
        editDate=(EditText)findViewById(R.id.date_text);

        editTitle.setOnTouchListener(mTouchListener);
        editDescription.setOnTouchListener(mTouchListener);
        editTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hasChanged = true;
                // Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return false;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (v == editTime) {

                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(EditorActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    String AMPM = "";

                                    mHour = hourOfDay;
                                    mMinute = minute;

                                    if(!DateFormat.is24HourFormat(getApplicationContext())){
                                        if(hourOfDay > 12) {
                                            hourOfDay -= 12;
                                            AMPM = getResources().getString(R.string.pm);
                                        }else if(hourOfDay == 0){
                                            hourOfDay += 12;
                                            AMPM = getResources().getString(R.string.am);;
                                        }else if(hourOfDay == 12){
                                            AMPM = getResources().getString(R.string.pm);;
                                        } else{
                                            AMPM = getResources().getString(R.string.am);;
                                        }

                                        if(minute < 10){
                                            editTime.setText(hourOfDay + ":" + "0" + minute + AMPM);
                                        }else{
                                            editTime.setText(hourOfDay + ":" + minute + AMPM);
                                        }
                                    }else{
                                        editTime.setText(hourOfDay + ":" + minute);
                                    }
                                }
                            }, mHour, mMinute, false);

                    timePickerDialog.show();
                }
                return false;
            }
        });

        editDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return false;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                hasChanged = true;
                if (v == editDate) {

                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(EditorActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    monthOfYear += 1;
                                    editDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                                    monthOfYear -= 1;
                                    mYear = year;
                                    mMonth = monthOfYear;
                                    mDay = dayOfMonth;

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
                return false;
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hasChanged = true;
                if(isChecked){
                    dateAndTimeLayout.setVisibility(View.VISIBLE);
                    toggleButton.setTextOn(getResources().getString(R.string.alarm_on));
                    mAlarm = ALARM_ON;
                }else{

                    dateAndTimeLayout.setVisibility(View.GONE);
                    toggleButton.setTextOff(getResources().getString(R.string.alarm_off));
                    mAlarm = ALARM_OFF;

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    private void saveTodo() {
        String titleString = editTitle.getText().toString().trim();
        String contentString = editDescription.getText().toString().trim();
        String dateReminderString = editDate.getText().toString().trim();
        String timeReminderString = editTime.getText().toString().trim();

        if (mToDoUri == null &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(contentString) && TextUtils.isEmpty(dateReminderString) && TextUtils.isEmpty(timeReminderString)){
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ToDoDBHelper.COLUMN_TITLE, titleString);
        values.put(ToDoDBHelper.COLUMN_CONTENT, contentString);
        values.put(ToDoDBHelper.COLUMN_REMAINER_TURN_ON, mAlarm);
        values.put(ToDoDBHelper.COLUMN_REMAINER_DATE_YEAR, mYear);
        values.put(ToDoDBHelper.COLUMN_REMAINER_DATE_MONTH, mMonth);
        values.put(ToDoDBHelper.COLUMN_REMAINER_DATE_DAY, mDay);
        values.put(ToDoDBHelper.COLUMN_REMAINER_TIME_HOUR, mHour);
        values.put(ToDoDBHelper.COLUMN_REMAINER_TIME_MINUTE, mMinute);
        values.put(ToDoDBHelper.COLUMN_DATE_TIME_CREATED, getDateTime());

        if (mToDoUri == null) {

            int notificationId = alarmUtils.getID();
            values.put(ToDoDBHelper.COLUMN_NOTIFICATION_ID, notificationId);
            Uri newUri = getContentResolver().insert(ToDoProvider.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, R.string.insert_todo_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.insert_todo_success,
                        Toast.LENGTH_SHORT).show();
                if(mAlarm == ALARM_ON){
                    if(Utils.isDateValid(mYear, mMonth, mDay, mHour,mMinute)){
                        saveAlarm(newUri, titleString, contentString, notificationId);
                    }
                }
            }
        } else {
            int rowsAffected = getContentResolver().update(mToDoUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.update_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.update_success,
                        Toast.LENGTH_SHORT).show();

                if(!toggleButton.isChecked() && mAlarmWasOn){
                    cancelAlarm(storeNotificationId);
                }

                if(mAlarm == ALARM_ON) {
                    if (Utils.isDateValid(mYear, mMonth, mDay, mHour, mMinute)) {
                        saveAlarm(mToDoUri, titleString, contentString, storeNotificationId);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveTodo();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if (!hasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ToDoDBHelper._ID,
                ToDoDBHelper.COLUMN_TITLE,
                ToDoDBHelper.COLUMN_CONTENT,
                ToDoDBHelper.COLUMN_REMAINER_TURN_ON,
                ToDoDBHelper.COLUMN_REMAINER_DATE_YEAR,
                ToDoDBHelper.COLUMN_REMAINER_DATE_MONTH,
                ToDoDBHelper.COLUMN_REMAINER_DATE_DAY,
                ToDoDBHelper.COLUMN_REMAINER_TIME_HOUR,
                ToDoDBHelper.COLUMN_REMAINER_TIME_MINUTE,
                ToDoDBHelper.COLUMN_NOTIFICATION_ID};

        return new CursorLoader(this, mToDoUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_TITLE);
            int contentColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_CONTENT);
            int yearColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_DATE_YEAR);
            int monthColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_DATE_MONTH);
            int dayColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_DATE_DAY);
            int hourColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_TIME_HOUR);
            int miunteColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_TIME_MINUTE);
            int reminderIDColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_NOTIFICATION_ID);
            int alarmOnColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_TURN_ON);

            String name = cursor.getString(nameColumnIndex);
            String content = cursor.getString(contentColumnIndex);
            storeNotificationId = cursor.getInt(reminderIDColumnIndex);
            mYear = cursor.getInt(yearColumnIndex);
            mMonth = cursor.getInt(monthColumnIndex);
            mDay = cursor.getInt(dayColumnIndex);
            mHour = cursor.getInt(hourColumnIndex);
            mMinute = cursor.getInt(miunteColumnIndex);

            String AMPM = "";

            editTitle.setText(name);
            editDescription.setText(content);
            editDate.setText(mDay+ "-" + mMonth + "-" + mYear);
            if(!DateFormat.is24HourFormat(getApplicationContext())){
                if(mHour > 12) {
                    mHour -= 12;
                    AMPM = getResources().getString(R.string.pm);
                }else if(mHour == 0){
                    mHour += 12;
                    AMPM = getResources().getString(R.string.am);;
                }else if(mHour == 12){
                    AMPM = getResources().getString(R.string.pm);;
                } else{
                    AMPM = getResources().getString(R.string.am);;
                }

                if(mMinute < 10){
                    editTime.setText(mHour + ":" + "0" + mMinute + AMPM);
                }else{
                    editTime.setText(mHour + ":" + mMinute + AMPM);
                }
            }else{
                editTime.setText(mHour + ":" + mMinute);
            }

            int alarmOn = cursor.getInt(alarmOnColumnIndex);
            if(alarmOn == ALARM_OFF){
                toggleButton.setChecked(false);
                mAlarmWasOn = false;
            }else{
                toggleButton.setChecked(true);
                mAlarmWasOn = true;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editTitle.setText("");
        editDescription.setText("");
        editTime.setText("");
        editDate.setText("");
        toggleButton.setChecked(false);
        mAlarmWasOn = false;

    }

    public void saveAlarm(Uri todoUri, String titleToSend, String messgaeToSend, int ID){
        alarmUtils.triggerAlarmManager(todoUri, titleToSend, messgaeToSend,ID, mYear, mMonth, mDay, mHour, mMinute);
    }

    public void cancelAlarm(int ID){
        try{
            alarmUtils.stopAlarmManager(ID);
        }catch(NullPointerException e){
            Toast.makeText(this, getResources().getString(R.string.alarm_calceled), Toast.LENGTH_SHORT).show();
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
