package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "newDB";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "myTable";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String SCRIPT = "create table " + TABLE_NAME + " ( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text"
            + ");";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(SCRIPT);
            db.setTransactionSuccessful();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
