package de.android.android2dz2part2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import data.DBHelper;

public class SecondActivity extends Activity implements View.OnClickListener,AdapterView.OnItemLongClickListener{
    private EditText editText;
    private LinearLayout linearLayoutRoot;
    private Button button;
    private ListView listView;
    private List<String> list;
    private Animation animation;
    private ArrayAdapter<String> adapter;
    private String companyName;

    static DBHelper dbHelper;
    static SQLiteDatabase db;
    private ContentValues cv;
    private Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        button.setOnClickListener(this);
    }
    void init() {
        editText = (EditText)findViewById(R.id.editText);
        companyName = getIntent().getStringExtra(MainActivity.COMPANY_NAME);
        editText.setHint(getResources().getString(R.string.enter_person_from) + " "
                + companyName + " " + getString(R.string.here));
        linearLayoutRoot = (LinearLayout)findViewById(R.id.linearLayoutRoot);
        linearLayoutRoot.setBackgroundColor(getResources().getColor(R.color.linearLayoutRoot));
        editText.setBackgroundColor(getResources().getColor(R.color.linearLayoutRoot));
        listView = (ListView)findViewById(R.id.listView);
        listView.setBackgroundColor(getResources().getColor(R.color.linearLayoutRoot));
        button = (Button)findViewById(R.id.button);
        load();

        adapter = new ArrayAdapter<>(SecondActivity.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    private void save() {
        db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_NAME, null, null);
        cv = new ContentValues();
        for (String s : list) {
            cv.put(DBHelper.COLUMN_NAME, s);
            db.insert(DBHelper.TABLE_NAME, null, cv);
        }
        db.close();
        cv.clear();
    }
    private List<String> load() {
        dbHelper = new DBHelper(this, DBHelper.DB_NAME + companyName, null, DBHelper.DB_VERSION);
        db = dbHelper.getWritableDatabase();
        cursor = db.query(DBHelper.TABLE_NAME, null,
                null, null, null, null, null);
        list = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
        }
        db.close();
        cursor.close();
        return list;
    }

    @Override
    public void onClick(View v) {
        animation = AnimationUtils.loadAnimation(SecondActivity.this, R.anim.rotate);
        button.setBackgroundColor(getResources().getColor(R.color.green));
        button.setAnimation(animation);
        list.add(0, editText.getText().toString());
        adapter.notifyDataSetChanged();
        editText.setText("");
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete)
                .setMessage(R.string.are_you_really_want_to_delete_this)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
                        button.setBackgroundColor(getResources().getColor(R.color.red));
                        button.startAnimation(animation);
                        adapter.remove(parent.getItemAtPosition(position).toString());
                        adapter.notifyDataSetChanged();
                        if (list.size() == 0) button.setBackgroundColor(getResources().getColor(R.color.base_color));
                    }
                }).create().show();
        return true;
    }
}
