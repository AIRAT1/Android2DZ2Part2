package de.android.android2dz2part2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import data.DBHelper;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemLongClickListener{
    public static final String LOG = "LOG";
    public static final String COMPANY_NAME = "company_name";
    private ListView listView;
    private EditText editText;
    private Button button;
    private ArrayAdapter<String> adapter;
    private Animation animation;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ContentValues cv;
    private Cursor cursor;

    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load();
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, String.valueOf(parent.getAdapter().getItem(position))
                        + " " + getString(R.string.is_selected), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra(COMPANY_NAME, String.valueOf(parent.getAdapter().getItem(position)));
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(this);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(this);
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
        dbHelper = new DBHelper(this, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
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
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        button.setBackgroundColor(getResources().getColor(R.color.green));
        button.startAnimation(animation);
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

                        if (SecondActivity.db != null) {
                            SecondActivity.db = SecondActivity.dbHelper.getWritableDatabase();
                            SecondActivity.db.delete(DBHelper.TABLE_NAME, null, null);
                        }

                        if (list.size() == 0) button.setBackgroundColor(getResources().getColor(R.color.base_color));
                    }
                }).create().show();
        return true;
    }
}
