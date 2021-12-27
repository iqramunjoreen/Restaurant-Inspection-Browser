package com.sfu.cmpt276groupproject.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sfu.cmpt276groupproject.SearchActivity;

import java.io.FileNotFoundException;

/**
 * A Database for searching feature
 */
// Code reference: [https://github.com/drbfraser/video-tutorial-code/blob/master/DatabaseToListViewDemo/src/ca/demo/databasetolistviewdemo/DBAdapter.java]
public class SearchDB {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG = "SearchDBAdapter";

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;

    // Setup fields
    public static final String KEY_TRACKINGNUM = "trackingNum";
    public static final String KEY_NAME = "name";
    public static final String KEY_HAZARD = "hazard";
    public static final String KEY_CRITICALNUM = "numberOfCritical";

    // Setup field numbers
    public static final int COL_TRACKINGNUM = 1;
    public static final int COL_NAME = 2;
    public static final int COL_HAZARD = 3;
    public static final int COL_CRITICALNUM = 4;

    public static final String[] ALL_KEYS = new String[]
            {KEY_ROWID, KEY_TRACKINGNUM, KEY_NAME, KEY_HAZARD, KEY_CRITICALNUM};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "SearchDB";
    public static final String DATABASE_TABLE = "SearchMainTable";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "

                    + KEY_TRACKINGNUM + " string not null, "
                    + KEY_NAME + " string not null, "
                    + KEY_HAZARD + " string not null, "
                    + KEY_CRITICALNUM + " integer not null"

                    // Rest  of creation:
                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper SearchDBHelper;
    private SQLiteDatabase db;

    private static SearchDB instance;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    public SearchDB(Context ctx) {
        this.context = ctx;
        SearchDBHelper = new DatabaseHelper(context);
    }

    /* Singleton code */

    public static SearchDB getInstance(Context context) {
        if (instance == null) {
            instance = new SearchDB(context);
        }
        return instance;
    }

    // Open the database connection.
    public SearchDB open() {
        db = SearchDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        SearchDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRow(String trackingNum, String name, String hazard, int criticalNum) {

        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRACKINGNUM, trackingNum);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_HAZARD, hazard);
        initialValues.put(KEY_CRITICALNUM, criticalNum);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll() {
        // deletes all rows efficiently with one query
        db.delete(DATABASE_TABLE, null, null);
    }

    // Return all data in the database.
    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


    // Code reference: [https://www.cnblogs.com/zhujiabin/p/4220051.html]
    // Code reference: [https://blog.csdn.net/dajian790626/article/details/17250193]
    public Cursor getSearchResult (String keyword, String hazardLevel, int criticalNum, String sign) {
        Cursor cursor;
        if (criticalNum == -1 || sign.equals("")) {
            cursor = db.query( DATABASE_TABLE, ALL_KEYS,
                    KEY_NAME + " like '%" + keyword + "%' and "
                            + KEY_HAZARD + " like " + "'%" + hazardLevel + "%'",
                    null, null, null, null);
        } else {
            if (sign.equals("<=")) {
                cursor = db.query(DATABASE_TABLE, ALL_KEYS,
                        KEY_NAME + " like '%" + keyword + "%' and "
                                + KEY_HAZARD + " like " + "'%" + hazardLevel + "%' and ("
                                + KEY_CRITICALNUM + " <= " + criticalNum + " and "
                                + KEY_CRITICALNUM + " > " + 0 + ")",
                        null, null, null, null);
            } else {
                cursor = db.query(DATABASE_TABLE, ALL_KEYS,
                        KEY_NAME + " like '%" + keyword + "%' and "
                                + KEY_HAZARD + " like " + "'%" + hazardLevel + "%' and "
                                + KEY_CRITICALNUM + " >= " + criticalNum,
                        null, null, null, null);
            }
        }
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
