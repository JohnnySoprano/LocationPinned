package com.example.noteme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
public class CoordinateDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "ndb2";
    private static final String DATABASE_TABLE = "ns3";

    // Column names for the notes table
    private static final String KEY_ID = "id";
    private static final String ADDRESS = "content";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
//    private static final String KEY_TIME = "time";
//    private static final String KEY_COLOR = "color";
//    private static final String KEY_IMAGE = "image";

    CoordinateDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DATABASE_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ADDRESS + " TEXT,"
                + LATITUDE + " FLOAT,"
                + LONGITUDE + " FLOAT"
                +")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) return;
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public long addNote(Coordinate coordinate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues calendar = new ContentValues();
        calendar.put(ADDRESS, coordinate.getAddress());
        calendar.put(LATITUDE, coordinate.getLatitude());
        calendar.put(LONGITUDE, coordinate.getLongitude());

        long ID = db.insert(DATABASE_TABLE, null, calendar);
        Log.d("Inserted", "ID ->" + ID);
//        Log.d("Color", "Color ->  " + note.getColor());
        return ID;
    }

    public Coordinate getNote(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID, LATITUDE, ADDRESS, LONGITUDE}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return new Coordinate(cursor.getLong(0), cursor.getString(1), cursor.getFloat(2), cursor.getFloat(3));
    }

    public List<Coordinate> getNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Coordinate> allCoordinates = new ArrayList<>();
        String query = "SELECT * FROM " + DATABASE_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Coordinate coordinate = new Coordinate();
                coordinate.setID(cursor.getLong(0));
                coordinate.setAddress(cursor.getString(1));
                coordinate.setLatitude(cursor.getFloat(2));
                coordinate.setLongitude(cursor.getFloat(3));
//                note.setTime(cursor.getString(4));
//                note.setColor(cursor.getString(5));
//                note.setImageByteArray(cursor.getBlob(6));
                allCoordinates.add(coordinate);
            } while (cursor.moveToNext());
        }
        return allCoordinates;
    }

    public void deleteNote(Coordinate coordinate) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(DATABASE_TABLE, KEY_ID + "=?", new String[]{String.valueOf(coordinate.getID())});
        Log.d("NoteDatabase", "Attempting to delete note with ID: " + coordinate.getID() + ". Rows deleted: " + deletedRows);
        db.close();
    }

    public int updateNote(Coordinate coordinate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ADDRESS, coordinate.getAddress());
        values.put(LATITUDE, coordinate.getLatitude());
        values.put(LONGITUDE, coordinate.getLongitude());

        // Updating row
        return db.update(DATABASE_TABLE, values, KEY_ID + " = ?", new String[]{String.valueOf(coordinate.getID())});
    }
}
