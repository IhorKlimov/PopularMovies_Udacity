package com.example.igorklimov.popularmoviesdemo.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByPopularity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByReleaseDate;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByVotes;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;

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
//        values.put(MovieByPopularity.COLUMN_TITLE, "Title3");
//        values.put(MovieByPopularity.COLUMN_POSTER, "Poster");
//        values.put(MovieByPopularity.COLUMN_RELEASE_DATE, "Release Date");
//        values.put(MovieByPopularity.COLUMN_GENRES, "Genres");
//        values.put(MovieByPopularity.COLUMN_AVERAGE_VOTE, "Votes");
//        values.put(MovieByPopularity.COLUMN_PLOT, "Plot");
//        ContentValues values2 = new ContentValues();
//        values2.put(MovieByPopularity.COLUMN_TITLE, "Title4");
//        values2.put(MovieByPopularity.COLUMN_POSTER, "Poster");
//        values2.put(MovieByPopularity.COLUMN_RELEASE_DATE, "Release Date");
//        values2.put(MovieByPopularity.COLUMN_GENRES, "Genres");
//        values2.put(MovieByPopularity.COLUMN_AVERAGE_VOTE, "Votes");
//        values2.put(MovieByPopularity.COLUMN_PLOT, "Plot");
//
//        long insert1 = db.insert(MovieByPopularity.TABLE_NAME, null, values);
//        long insert2 = db.insert(MovieByPopularity.TABLE_NAME, null, values2);
//
//        Log.d(LOG_TAG, insert1 + " " + insert2);
//
//        Cursor cursor = db.query(MovieByPopularity.TABLE_NAME, null, null, null, null, null, null);
//        assertTrue(cursor.moveToFirst());
//
//        cursor.close();
//        db.close();
//    }

//    public void testReadDb() {
//        SQLiteDatabase db = new MoviesDbHelper(mContext).getReadableDatabase();
//        Cursor cursor = db.query(MovieByPopularity.TABLE_NAME, null, null, null, null, null, null);
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
//        values.put(MovieByPopularity.COLUMN_TITLE, "Title");
//        values.put(MovieByPopularity.COLUMN_POSTER, "Poster");
//        values.put(MovieByPopularity.COLUMN_RELEASE_DATE, "Release Date");
//        values.put(MovieByPopularity.COLUMN_GENRES, "Genres");
//        values.put(MovieByPopularity.COLUMN_AVERAGE_VOTE, "Votes");
//        values.put(MovieByPopularity.COLUMN_PLOT, "Plot");
//
//        Uri insert = contentResolver.insert(MovieByPopularity.CONTENT_URI, values);
//        Log.d(LOG_TAG, "testInsert: " + insert);
//
//        ContentValues values2 = new ContentValues();
//        values2.put(MovieByPopularity.COLUMN_TITLE, "Title");
//        values2.put(MovieByPopularity.COLUMN_POSTER, "Poster");
//        values2.put(MovieByPopularity.COLUMN_RELEASE_DATE, "Release Date");
//        values2.put(MovieByPopularity.COLUMN_GENRES, "Genres");
//        values2.put(MovieByPopularity.COLUMN_AVERAGE_VOTE, "Votes");
//        values2.put(MovieByPopularity.COLUMN_PLOT, "Plot");
//
//        Uri insert2 = contentResolver.insert(MovieByPopularity.CONTENT_URI, values);
//        Log.d(LOG_TAG, "testInsert: " + insert2);
//
//        ContentValues values3 = new ContentValues();
//        values3.put(MovieByPopularity.COLUMN_TITLE, "Title");
//        values3.put(MovieByPopularity.COLUMN_POSTER, "Poster");
//        values3.put(MovieByPopularity.COLUMN_RELEASE_DATE, "Release Date");
//        values3.put(MovieByPopularity.COLUMN_GENRES, "Genres");
//        values3.put(MovieByPopularity.COLUMN_AVERAGE_VOTE, "Votes");
//        values3.put(MovieByPopularity.COLUMN_PLOT, "Plot");
//
//        Uri insert3 = contentResolver.insert(MovieByPopularity.CONTENT_URI, values);
//        Log.d(LOG_TAG, "testInsert: " + insert3);
//
//        ContentValues values4 = new ContentValues();
//        values4.put(MovieByPopularity.COLUMN_TITLE, "Title");
//        values4.put(MovieByPopularity.COLUMN_POSTER, "Poster");
//        values4.put(MovieByPopularity.COLUMN_RELEASE_DATE, "Release Date");
//        values4.put(MovieByPopularity.COLUMN_GENRES, "Genres");
//        values4.put(MovieByPopularity.COLUMN_AVERAGE_VOTE, "Votes");
//        values4.put(MovieByPopularity.COLUMN_PLOT, "Plot");
//
//        Uri insert4 = contentResolver.insert(MovieByPopularity.CONTENT_URI, values);
//        Log.d(LOG_TAG, "testInsert: " + insert4);
//    }

//    public void testQuery() {
//        ContentResolver contentResolver = mContext.getContentResolver();
//        Cursor cursor = contentResolver.query(MovieByPopularity.CONTENT_URI, null, null, null, null);
//        assertTrue(cursor != null);
//
//        while (cursor.moveToNext()) {
//            Log.d(LOG_TAG, "testQuery: " + cursor.getString(1));
//        }
//        cursor.close();
//
//        Cursor cursor2 = contentResolver.query(MovieByPopularity.buildMovieUri(1), null, null, null, null);
//        assertTrue(cursor2 != null);
//        while (cursor2.moveToNext()) {
//            Log.d(LOG_TAG, "testQuery: 2 " + cursor2.getString(1));
//        }
//        cursor2.close();
//    }

//    public void testDelete() {
//        ContentResolver contentResolver = mContext.getContentResolver();
//        Cursor cursor = contentResolver.query(MovieByPopularity.CONTENT_URI, null, null, null, null);
//        assertTrue(cursor != null);
//        Log.d(LOG_TAG, "testDelete: cursor size " + cursor.getCount());
//        int delete = contentResolver.delete(MovieByPopularity.buildMovieUri(3), null, null);
//        Log.d(LOG_TAG, "testDelete: " + delete);
//        Log.d(LOG_TAG, "testDelete: cursor size " + cursor.getCount());
//
//    }

    public void testBulkInsert() {
//        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
        ContentResolver contentResolver = mContext.getContentResolver();
//        ArrayList<ContentValues> values = new ArrayList<>();

        ContentValues value1 = new ContentValues();
        value1.put(MovieContract.COLUMN_TITLE, "Title");
        value1.put(MovieContract.COLUMN_POSTER, "Poster");
        value1.put(MovieContract.COLUMN_RELEASE_DATE, "Release Date");
        value1.put(MovieContract.COLUMN_GENRES, "Genres");
        value1.put(MovieContract.COLUMN_AVERAGE_VOTE, "Votes");
        value1.put(MovieContract.COLUMN_PLOT, "Plot");

        Uri insert = contentResolver.insert(MovieByPopularity.CONTENT_URI, value1);
        Cursor cursor = contentResolver.query(MovieByPopularity.CONTENT_URI, null, null, null, null);
        assertTrue(cursor != null);
        assertTrue(cursor.getCount() > 0);
        Log.d(LOG_TAG, "testBulkInsert: " + insert);
        Log.d(LOG_TAG, "testBulkInsert: MovieByPopularity cursor count: " + cursor.getCount());
        cursor.close();

        Uri insert2 = contentResolver.insert(MovieByReleaseDate.CONTENT_URI, value1);
        Cursor cursor2 = contentResolver.query(MovieByReleaseDate.CONTENT_URI, null, null, null, null);
        assertTrue(cursor2 != null);
        assertTrue(cursor2.getCount() > 0);
        Log.d(LOG_TAG, "testBulkInsert: " + insert2);
        Log.d(LOG_TAG, "testBulkInsert: MovieByReleaseDate cursor count: " + cursor2.getCount());
        cursor2.close();

        Uri insert3 = contentResolver.insert(MovieByVotes.CONTENT_URI, value1);
        Cursor cursor3 = contentResolver.query(MovieByVotes.CONTENT_URI, null, null, null, null);
        assertTrue(cursor3 != null);
        assertTrue(cursor3.getCount() > 0);
        Log.d(LOG_TAG, "testBulkInsert: " + insert3);
        Log.d(LOG_TAG, "testBulkInsert: MovieByVotes cursor count: " + cursor3.getCount());
        cursor3.close();
    }

}
