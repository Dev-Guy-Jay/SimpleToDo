package com.dev_guy_jay.simpletodo.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.dev_guy_jay.simpletodo.R;
import com.dev_guy_jay.simpletodo.data.ToDoDBHelper;

import java.util.ArrayList;

public class ToDoAdapter extends CursorAdapter{

    public ToDoAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){

        TextView titleView = (TextView) view.findViewById(R.id.item);
        TextView timeView = (TextView) view.findViewById(R.id.time);
        String title = cursor.getString(cursor.getColumnIndex(ToDoDBHelper.COLUMN_TITLE));
        String time = cursor.getString(cursor.getColumnIndex(ToDoDBHelper.COLUMN_DATE_TIME_CREATED));
        titleView.setText(title);
        timeView.setText(context.getResources().getString(R.string.last_edit) + time);

    }

}
