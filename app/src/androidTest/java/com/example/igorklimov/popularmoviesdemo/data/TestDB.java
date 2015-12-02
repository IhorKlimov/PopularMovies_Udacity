package com.example.igorklimov.popularmoviesdemo.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry;

import java.util.ArrayList;

/**
 * Created by Igor Klimov on 11/27/2015.
 */
public class TestDB extends AndroidTestCase {

    private static final String LOG_TAG = "TestDb";

//    public void testCreateDb() {
//        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
//
//        SQLiteDatabase db = new MoviesDbHelper(mContext).getWritableDatabase();
//        assertTrue(db.isOpen());
//        ContentValues values = new ContentValues();
//        values.put(MovieEntry.COLUMN_TITLE, "Title3");
//        values.put(MovieEntry.COLUMN_POSTER, "Poster");
//        values.put(MovieEntry.COLUMN_RELEASE_DATE, "Release Date");
//        values.put(MovieEntry.COLUMN_GENRES, "Genres");
//        values.put(MovieEntry.COLUMN_AVERAGE_VOTE, "Votes");
//        values.put(MovieEntry.COLUMN_PLOT, "Plot");
//        ContentValues values2 = new ContentValues();
//        values2.put(MovieEntry.COLUMN_TITLE, "Title4");
//        values2.put(MovieEntry.COLUMN_POSTER, "Poster");
//        values2.put(MovieEntry.COLUMN_RELEASE_DATE, "Release Date");
//        values2.put(MovieEntry.COLUMN_GENRES, "Genres");
//        values2.put(MovieEntry.COLUMN_AVERAGE_VOTE, "Votes");
//        values2.put(MovieEntry.COLUMN_PLOT, "Plot");
//
//        long insert1 = db.insert(MovieEntry.TABLE_NAME, null, values);
//        long insert2 = db.insert(MovieEntry.TABLE_NAME, null, values2);
//
//        Log.d(LOG_TAG, insert1 + " " + insert2);
//
//        Cursor cursor = db.query(MovieEntry.TABLE_NAME, null, null, null, null, null, null);
//        assertTrue(cursor.moveToFirst());
//
//        cursor.close();
//        db.close();
//    }

//    public void testReadDb() {
//        SQLiteDatabase db = new MoviesDbHelper(mContext).getReadableDatabase();
//        Cursor cursor = db.query(MovieEntry.TABLE_NAME, null, null, null, null, null, null);
//        assertTrue(cursor.moveToFirst());
//
//        Log.d(LOG_TAG, cursor.getString(0));
//
//        while (cursor.moveToNext()) {
//            Log.d(LOG_TAG, cursor.getString(0));
//        }
//
//
//        for (int i = 20; i < 40; i++) {
//            if (cursor.moveToPosition(i)) {
//                Log.d(LOG_TAG, cursor.getString(0) + "");
//            }
//        }
//
//        cursor.close();
//        db.close();
//
//    }

//    public void testInsert() {
//        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
//        ContentResolver contentResolver = mContext.getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put(MovieEntry.COLUMN_TITLE, "Title");
//        values.put(MovieEntry.COLUMN_POSTER, "Poster");
//        values.put(MovieEntry.COLUMN_RELEASE_DATE, "Release Date");
//        values.put(MovieEntry.COLUMN_GENRES, "Genres");
//        values.put(MovieEntry.COLUMN_AVERAGE_VOTE, "Votes");
//        values.put(MovieEntry.COLUMN_PLOT, "Plot");
//
//        Uri insert = contentResolver.insert(MovieEntry.CONTENT_URI, values);
//        Log.d(LOG_TAG, "testInsert: " + insert);
//
//        ContentValues values2 = new ContentValues();
//        values2.put(MovieEntry.COLUMN_TITLE, "Title");
//        values2.put(MovieEntry.COLUMN_POSTER, "Poster");
//        values2.put(MovieEntry.COLUMN_RELEASE_DATE, "Release Date");
//        values2.put(MovieEntry.COLUMN_GENRES, "Genres");
//        values2.put(MovieEntry.COLUMN_AVERAGE_VOTE, "Votes");
//        values2.put(MovieEntry.COLUMN_PLOT, "Plot");
//
//        Uri insert2 = contentResolver.insert(MovieEntry.CONTENT_URI, values);
//        Log.d(LOG_TAG, "testInsert: " + insert2);
//
//        ContentValues values3 = new ContentValues();
//        values3.put(MovieEntry.COLUMN_TITLE, "Title");
//        values3.put(MovieEntry.COLUMN_POSTER, "Poster");
//        values3.put(MovieEntry.COLUMN_RELEASE_DATE, "Release Date");
//        values3.put(MovieEntry.COLUMN_GENRES, "Genres");
//        values3.put(MovieEntry.COLUMN_AVERAGE_VOTE, "Votes");
//        values3.put(MovieEntry.COLUMN_PLOT, "Plot");
//
//        Uri insert3 = contentResolver.insert(MovieEntry.CONTENT_URI, values);
//        Log.d(LOG_TAG, "testInsert: " + insert3);
//
//        ContentValues values4 = new ContentValues();
//        values4.put(MovieEntry.COLUMN_TITLE, "Title");
//        values4.put(MovieEntry.COLUMN_POSTER, "Poster");
//        values4.put(MovieEntry.COLUMN_RELEASE_DATE, "Release Date");
//        values4.put(MovieEntry.COLUMN_GENRES, "Genres");
//        values4.put(MovieEntry.COLUMN_AVERAGE_VOTE, "Votes");
//        values4.put(MovieEntry.COLUMN_PLOT, "Plot");
//
//        Uri insert4 = contentResolver.insert(MovieEntry.CONTENT_URI, values);
//        Log.d(LOG_TAG, "testInsert: " + insert4);
//    }

//    public void testQuery() {
//        ContentResolver contentResolver = mContext.getContentResolver();
//        Cursor cursor = contentResolver.query(MovieEntry.CONTENT_URI, null, null, null, null);
//        assertTrue(cursor != null);
//
//        while (cursor.moveToNext()) {
//            Log.d(LOG_TAG, "testQuery: " + cursor.getString(1));
//        }
//        cursor.close();
//
//        Cursor cursor2 = contentResolver.query(MovieEntry.buildMovieUri(1), null, null, null, null);
//        assertTrue(cursor2 != null);
//        while (cursor2.moveToNext()) {
//            Log.d(LOG_TAG, "testQuery: 2 " + cursor2.getString(1));
//        }
//        cursor2.close();
//    }

//    public void testDelete() {
//        ContentResolver contentResolver = mContext.getContentResolver();
//        Cursor cursor = contentResolver.query(MovieEntry.CONTENT_URI, null, null, null, null);
//        assertTrue(cursor != null);
//        Log.d(LOG_TAG, "testDelete: cursor size " + cursor.getCount());
//        int delete = contentResolver.delete(MovieEntry.buildMovieUri(3), null, null);
//        Log.d(LOG_TAG, "testDelete: " + delete);
//        Log.d(LOG_TAG, "testDelete: cursor size " + cursor.getCount());
//
//    }

    public void testBulkInsert() {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
        ContentResolver contentResolver = mContext.getContentResolver();
        ArrayList<ContentValues> values = new ArrayList<>();

        ContentValues value1 = new ContentValues();
        value1.put(MovieEntry.COLUMN_TITLE, "Title");
        value1.put(MovieEntry.COLUMN_POSTER, "Poster");
        value1.put(MovieEntry.COLUMN_RELEASE_DATE, "Release Date");
        value1.put(MovieEntry.COLUMN_GENRES, "Genres");
        value1.put(MovieEntry.COLUMN_AVERAGE_VOTE, "Votes");
        value1.put(MovieEntry.COLUMN_PLOT, "Plot");

        ContentValues value2 = new ContentValues();
        value2.put(MovieEntry.COLUMN_TITLE, "Title2");
        value2.put(MovieEntry.COLUMN_POSTER, "Poster");
        value2.put(MovieEntry.COLUMN_RELEASE_DATE, "Release Date");
        value2.put(MovieEntry.COLUMN_GENRES, "Genres");
        value2.put(MovieEntry.COLUMN_AVERAGE_VOTE, "Votes");
        value2.put(MovieEntry.COLUMN_PLOT, "Plot");

        ContentValues value3 = new ContentValues();
        value3.put(MovieEntry.COLUMN_TITLE, "Title3");
        value3.put(MovieEntry.COLUMN_POSTER, "Poster");
        value3.put(MovieEntry.COLUMN_RELEASE_DATE, "Release Date");
        value3.put(MovieEntry.COLUMN_GENRES, "Genres");
        value3.put(MovieEntry.COLUMN_AVERAGE_VOTE, "Votes");
        value3.put(MovieEntry.COLUMN_PLOT, "Plot");

        values.add(value1);
        values.add(value2);
        values.add(value3);

        ContentValues[] objects =  values.toArray(new ContentValues[values.size()]);

        int bulkInsert = contentResolver.bulkInsert(MovieEntry.CONTENT_URI, objects);
        assertTrue(bulkInsert == 3);

        Log.d(LOG_TAG, "testBulkInsert: "+ bulkInsert);
    }

}
