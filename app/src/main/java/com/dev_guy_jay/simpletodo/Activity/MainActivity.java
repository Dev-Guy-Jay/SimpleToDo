package com.dev_guy_jay.simpletodo.Activity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.GridView;
import com.dev_guy_jay.simpletodo.R;
import com.dev_guy_jay.simpletodo.ToDo.ToDo;
import com.dev_guy_jay.simpletodo.Adapter.ToDoAdapter;
import com.dev_guy_jay.simpletodo.data.ToDoDBHelper;
import com.dev_guy_jay.simpletodo.data.ToDoProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TODO_LOADER = 0;

    ToDoDBHelper toDoHelper;

    ArrayList<ToDo> todo;
    ToDoAdapter adapter;
    GridView listview;
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("");

        toDoHelper = new ToDoDBHelper(this);

        todo = new ArrayList<>();
        adapter = new ToDoAdapter(this, null);

        listview = findViewById(R.id.list);
        emptyView = findViewById(R.id.empty_view);
        listview.setEmptyView(emptyView);
        listview.setAdapter(adapter);
        registerForContextMenu(listview);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Uri currentToDoUri = ContentUris.withAppendedId(ToDoProvider.CONTENT_URI, id);

                intent.setData(currentToDoUri);

                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        getLoaderManager().initLoader(TODO_LOADER,null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ToDoDBHelper._ID,
                ToDoDBHelper.COLUMN_TITLE,
                ToDoDBHelper.COLUMN_DATE_TIME_CREATED};

        return new CursorLoader(this, ToDoProvider.CONTENT_URI, projection,null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        adapter.swapCursor(null);
    }
}
