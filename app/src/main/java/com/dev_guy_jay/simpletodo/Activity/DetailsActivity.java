package com.dev_guy_jay.simpletodo.Activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dev_guy_jay.simpletodo.R;
import com.dev_guy_jay.simpletodo.Utils.AlarmUtils;
import com.dev_guy_jay.simpletodo.data.ToDoDBHelper;
import com.dev_guy_jay.simpletodo.data.ToDoProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.dev_guy_jay.simpletodo.Activity.MainActivity.TODO_LOADER;
import static com.dev_guy_jay.simpletodo.data.ToDoDBHelper.ALARM_OFF;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final String DetailsLog = getClass().getCanonicalName();

    AlarmUtils alarmUtils;

    private Uri mToDoUri;

    TextView titleText;
    TextView contentText;
    TextView alarmText;
    int todoID;
    int storeNotificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        alarmUtils = new AlarmUtils(getApplicationContext());

        Intent intent = getIntent();
        Bundle todoID = intent.getExtras();
        /*
        try{
            newTodoID = todoID.getLong("ID");
        }catch(NullPointerException e){
            Toast.makeText(this, getResources().getString(R.string.todo_no_longer_exist), Toast.LENGTH_SHORT).show();

            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        */
        mToDoUri= intent.getData();

        titleText = (TextView) findViewById(R.id.title_text);
        contentText = (TextView) findViewById(R.id.content_text);
        alarmText = (TextView) findViewById(R.id.alarm_text);

        contentText.setMovementMethod(new ScrollingMovementMethod());

        setTitle(R.string.details);

        getLoaderManager().initLoader(TODO_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:

                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);

                intent.setData(mToDoUri);

                startActivity(intent);
                return true;

            case R.id.action_delete:
                String[] args = new String[] {String.valueOf(todoID)};
                alarmUtils.stopAlarmManager(storeNotificationId);
                getContentResolver().delete(ToDoProvider.CONTENT_URI, ToDoDBHelper.COLUMN_ID + "=?", args );
                finish();
                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

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

        return new CursorLoader(this,
                mToDoUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int todoIDColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_ID);
            int nameColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_TITLE);
            int contentColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_CONTENT);
            int yearColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_DATE_YEAR);
            int monthColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_DATE_MONTH);
            int dayColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_DATE_DAY);
            int hourColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_TIME_HOUR);
            int miunteColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_TIME_MINUTE);
            int alarmOnColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_REMAINER_TURN_ON);
            int reminderIDColumnIndex = cursor.getColumnIndex(ToDoDBHelper.COLUMN_NOTIFICATION_ID);

            todoID = cursor.getInt(todoIDColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String content = cursor.getString(contentColumnIndex);
            int year = cursor.getInt(yearColumnIndex);
            int month = cursor.getInt(monthColumnIndex);
            int day = cursor.getInt(dayColumnIndex);
            int hour = cursor.getInt(hourColumnIndex);
            int minute = cursor.getInt(miunteColumnIndex);
            storeNotificationId = cursor.getInt(reminderIDColumnIndex);

            titleText.setText(name);
            contentText.setText(content);
            String monthName = convertMonthToString(month);

            String AMPM = "";

            if(!DateFormat.is24HourFormat(getApplicationContext())){
                if(hour > 12) {
                    hour -= 12;
                    AMPM = getResources().getString(R.string.pm);
                }else if(hour == 0){
                    hour += 12;
                    AMPM = getResources().getString(R.string.am);
                }else if(hour == 12){
                    AMPM = getResources().getString(R.string.pm);
                } else{
                    AMPM = getResources().getString(R.string.am);
                }

                if(minute < 10){
                    alarmText.setText(getResources().getString(R.string.alarm_set) + day+ "-" + monthName + "-" + year + " " + hour + ":" + "0" + minute + AMPM);
                }else{
                    alarmText.setText(getResources().getString(R.string.alarm_set) + day+ "-" + monthName + "-" + year + " " + hour + ":" + minute + AMPM);
                }
            }else{
                alarmText.setText(getResources().getString(R.string.alarm_set) + day+ "-" + monthName + "-" + year + " " + hour + ":" + minute);
            }
            int isAlarmOn = cursor.getInt(alarmOnColumnIndex);
            if(isAlarmOn == ALARM_OFF){
                alarmText.setVisibility(View.GONE);
            }else{
                alarmText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }

    public String convertMonthToString(int monthToConvert) {
        Calendar cal= Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        cal.set(Calendar.MONTH,monthToConvert);
        return month_date.format(cal.getTime());
    }
}
