package com.example.igorklimov.popularmoviesdemo.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry;

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

    public void testReadDb() {
        SQLiteDatabase db = new MoviesDbHelper(mContext).getReadableDatabase();
        Cursor cursor = db.query(MovieEntry.TABLE_NAME, null, null, null, null, null, null);
//        assertTrue(cursor.moveToFirst());

//        Log.d(LOG_TAG, cursor.getString(0));

//        while (cursor.moveToNext()) {
//            Log.d(LOG_TAG, cursor.getString(0));
//        }


        for (int i = 20; i < 40; i++) {
            if (cursor.moveToPosition(i)) {
                Log.d(LOG_TAG, cursor.getString(0) + "");
            }
        }

        cursor.close();
        db.close();

    }




}
