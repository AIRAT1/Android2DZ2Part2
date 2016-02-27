package data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {
    static final String AUTHORITY = "AUTHORITY";
    static final String CONTACT_PATH = "path";
    public static final Uri CONTACT_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CONTACT_PATH);
    static final String CONTACT_CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd."
                    + AUTHORITY + "." + CONTACT_PATH;
    static final String CONTACT_CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd."
                    + AUTHORITY + "." + CONTACT_PATH;
    static final int URI_CONTACTS = 1;
    static final int URI_CONTACTS_ID = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTACT_PATH, URI_CONTACTS);
        uriMatcher.addURI(AUTHORITY, CONTACT_PATH + "/#", URI_CONTACTS_ID);
    }
    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext(), DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                break;
            case URI_CONTACTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = DBHelper.COLUMN_ID + " = " + id;
                }else {
                    selection += " AND " + DBHelper.COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), CONTACT_CONTENT_URI);
//        db.close();
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_CONTACTS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        long rowId = db.insert(DBHelper.TABLE_NAME, null, values);
        Uri resultUri = ContentUris.withAppendedId(CONTACT_CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(resultUri, null);
//        db.close();
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                break;
            case URI_CONTACTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = DBHelper.COLUMN_ID + " = " + id;
                }else {
                    selection += " AND " + DBHelper.COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(DBHelper.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
//        db.close();
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                break;
            case URI_CONTACTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = DBHelper.COLUMN_ID + " = " + id;
                }else {
                    selection = " AND " + DBHelper.COLUMN_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(DBHelper.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
//        db.close();
        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                return CONTACT_CONTENT_TYPE;
            case URI_CONTACTS_ID:
                return CONTACT_CONTENT_ITEM_TYPE;
        }
        return null;
    }
}
