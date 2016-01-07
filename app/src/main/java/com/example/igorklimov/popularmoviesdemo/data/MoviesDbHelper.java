package com.example.igorklimov.popularmoviesdemo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.data.MovieContract.Details;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.FavoriteMovie;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByPopularity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByReleaseDate;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByVotes;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.Review;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;

/**
 * Created by Igor Klimov on 11/28/2015.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "Movies.db";
    private final Context context;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("TAG", "CREATING DB");
        db.execSQL(MovieByPopularity.SQL_CREATE_ENTRIES);
        db.execSQL(MovieByReleaseDate.SQL_CREATE_ENTRIES);
        db.execSQL(MovieByVotes.SQL_CREATE_ENTRIES);
        db.execSQL(FavoriteMovie.SQL_CREATE_ENTRIES);
        db.execSQL(Details.SQL_CREATE_ENTRIES);
        db.execSQL(Review.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("TAG", "UPDATING DB");
        Utility.initializePagePreference(context);
        db.execSQL(MovieByPopularity.SQL_DELETE_ENTRIES);
        db.execSQL(MovieByReleaseDate.SQL_DELETE_ENTRIES);
        db.execSQL(MovieByVotes.SQL_DELETE_ENTRIES);
        db.execSQL(FavoriteMovie.SQL_DELETE_ENTRIES);
        db.execSQL(Details.SQL_DELETE_ENTRIES);
        db.execSQL(Review.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
