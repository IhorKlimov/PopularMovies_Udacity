/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    private final Context mContext;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
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
        Utility.initializePagePreference(mContext);
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
