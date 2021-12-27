package com.sfu.cmpt276groupproject.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A Database for favourites
 */
public class FavoriteDB extends SQLiteOpenHelper {
    private static final String TAG = "FavoriteDB";

    private static final String TABLE_NAME = "FAVORITE_RESTAURANTS";
    private static final String DATABASE_NAME = "favorite_restaurants.db";
    private static final String COL1 = "RestaurantName";
    private static final String COL2 = "TrackingNumber";


    public FavoriteDB(Context context){
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + TABLE_NAME + "(" +
                COL1 + " CHAR(256), " +
                COL2 + " CHAR(256), " +
                "PRIMARY KEY (" + COL2 +")" +
                ")";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addFavorite(String restaurantName, String trackingNum){
        restaurantName = restaurantName.replace(" ", "");
        trackingNum = trackingNum.replace("-", "");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, restaurantName);
        contentValues.put(COL2, trackingNum);

        Log.d(TAG, "Adding " + restaurantName);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return (result != -1);
    }

    public boolean deleteFavorite(String trackingNum){
        trackingNum = trackingNum.replace("-", "");
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, COL2 + " = " + "'" + trackingNum + "'", null);
        return (result != -1);
    }
    public boolean isFound(String trackingNum){
        trackingNum = trackingNum.replace("-", "");
        trackingNum = trackingNum.trim();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +  " WHERE " + COL2 + " = '" + trackingNum +"'", null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }
}
