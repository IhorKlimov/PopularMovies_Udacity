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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.igorklimov.popularmoviesdemo.data.MovieContract.Details;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.FavoriteMovie;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByPopularity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByReleaseDate;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByVotes;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.Review;

import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.CONTENT_AUTHORITY;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_DETAILS;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_FAVORITE_MOVIE;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_MOVIE_BY_POPULARITY;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_MOVIE_BY_RELEASE_DATE;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_MOVIE_BY_VOTES;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_REVIEW;

/**
 * Created by Igor Klimov on 11/29/2015.
 */
public class Provider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mMoviesDbHelper;
    private ContentResolver mContentResolver;

    private static final int MOVIE_BY_POPULARITY = 100;
    private static final int MOVIE_BY_POPULARITY_WITH_ID = 101;
    private static final int MOVIE_BY_RELEASE_DATE = 200;
    private static final int MOVIE_BY_RELEASE_DATE_WITH_ID = 201;
    private static final int MOVIE_BY_VOTES = 300;
    private static final int MOVIE_BY_VOTES_WITH_ID = 301;
    private static final int FAVORITE_MOVIE = 400;
    private static final int FAVORITE_MOVIE_WITH_ID = 401;
    private static final int MOVIE_DETAILS = 500;
    private static final int MOVIE_REVIEW = 600;


    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new MoviesDbHelper(getContext());
        if (getContext() != null) mContentResolver = getContext().getContentResolver();
        return true;
    }

    /*
     * Return no type for MIME type
     */
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mMoviesDbHelper.getReadableDatabase();
        Cursor cursor;
        String id;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_BY_POPULARITY:
                cursor = db.query(MovieByPopularity.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_BY_POPULARITY_WITH_ID:
                id = MovieByPopularity.getIdFromUri(uri);
                cursor = db.query(MovieByPopularity.TABLE_NAME, projection,
                        MovieByPopularity._ID + "=?", new String[]{id}, null, null, sortOrder);
                break;
            case MOVIE_BY_RELEASE_DATE:
                cursor = db.query(MovieByReleaseDate.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_BY_RELEASE_DATE_WITH_ID:
                id = MovieByReleaseDate.getIdFromUri(uri);
                cursor = db.query(MovieByReleaseDate.TABLE_NAME, projection,
                        MovieByReleaseDate._ID + "=?", new String[]{id}, null, null, sortOrder);
                break;
            case MOVIE_BY_VOTES:
                cursor = db.query(MovieByVotes.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_BY_VOTES_WITH_ID:
                id = MovieByVotes.getIdFromUri(uri);
                cursor = db.query(MovieByVotes.TABLE_NAME, projection,
                        MovieByVotes._ID + "=?", new String[]{id}, null, null, sortOrder);
                break;
            case FAVORITE_MOVIE:
                cursor = db.query(FavoriteMovie.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAVORITE_MOVIE_WITH_ID:
                id = FavoriteMovie.getIdFromUri(uri);
                cursor = db.query(FavoriteMovie.TABLE_NAME, projection,
                        FavoriteMovie._ID + "=?", new String[]{id}, null, null, sortOrder);
                break;
            case MOVIE_DETAILS:
                cursor = db.query(Details.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_REVIEW:
                cursor = db.query(Review.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        cursor.setNotificationUri(mContentResolver, uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        Uri result;
        long insert;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_BY_POPULARITY:
                insert = db.insert(MovieByPopularity.TABLE_NAME, null, values);
                result = MovieByPopularity.buildMovieUri(insert);
                break;
            case MOVIE_BY_RELEASE_DATE:
                insert = db.insert(MovieByReleaseDate.TABLE_NAME, null, values);
                result = MovieByReleaseDate.buildMovieUri(insert);
                break;
            case MOVIE_BY_VOTES:
                insert = db.insert(MovieByVotes.TABLE_NAME, null, values);
                result = MovieByVotes.buildMovieUri(insert);
                break;
            case FAVORITE_MOVIE:
                insert = db.insert(FavoriteMovie.TABLE_NAME, null, values);
                result = FavoriteMovie.buildMovieUri(insert);
                break;
            case MOVIE_DETAILS:
                insert = db.insert(Details.TABLE_NAME, null, values);
                result = Details.buildMovieUri(insert);
                break;
            case MOVIE_REVIEW:
                insert = db.insert(Review.TABLE_NAME, null, values);
                result = Review.buildMovieUri(insert);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        mContentResolver.notifyChange(uri, null);
        return result;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int inserted = 0;
        SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MOVIE_BY_POPULARITY:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long insert = db.insert(MovieByPopularity.TABLE_NAME, null, value);
                        if (insert != -1) inserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case MOVIE_BY_RELEASE_DATE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long insert = db.insert(MovieByReleaseDate.TABLE_NAME, null, value);
                        if (insert != -1) inserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case MOVIE_BY_VOTES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long insert = db.insert(MovieByVotes.TABLE_NAME, null, value);
                        if (insert != -1) inserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case FAVORITE_MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long insert = db.insert(FavoriteMovie.TABLE_NAME, null, value);
                        if (insert != -1) inserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case MOVIE_REVIEW:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long insert = db.insert(Review.TABLE_NAME, null, value);
                        if (insert != -1) inserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (inserted != 0) mContentResolver.notifyChange(uri, null);
        return inserted;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int deleted;
        String id;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_BY_POPULARITY:
                deleted = db.delete(MovieByPopularity.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_BY_POPULARITY_WITH_ID:
                id = MovieByPopularity.getIdFromUri(uri);
                deleted = db.delete(MovieByPopularity.TABLE_NAME, MovieByPopularity._ID + "=?", new String[]{id});
                break;
            case MOVIE_BY_RELEASE_DATE:
                deleted = db.delete(MovieByReleaseDate.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_BY_RELEASE_DATE_WITH_ID:
                id = MovieByReleaseDate.getIdFromUri(uri);
                deleted = db.delete(MovieByReleaseDate.TABLE_NAME, MovieByReleaseDate._ID + "=?", new String[]{id});
                break;
            case MOVIE_BY_VOTES:
                deleted = db.delete(MovieByVotes.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_BY_VOTES_WITH_ID:
                id = MovieByVotes.getIdFromUri(uri);
                deleted = db.delete(MovieByVotes.TABLE_NAME, MovieByVotes._ID + "=?", new String[]{id});
                break;
            case FAVORITE_MOVIE:
                deleted = db.delete(FavoriteMovie.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_MOVIE_WITH_ID:
                id = FavoriteMovie.getIdFromUri(uri);
                deleted = db.delete(FavoriteMovie.TABLE_NAME, FavoriteMovie._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException();
        }
        if (deleted > 0) mContentResolver.notifyChange(uri, null);

        return deleted;
    }

    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Utility method to get the intention
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_POPULARITY, MOVIE_BY_POPULARITY);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_POPULARITY + "/#", MOVIE_BY_POPULARITY_WITH_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_RELEASE_DATE, MOVIE_BY_RELEASE_DATE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_RELEASE_DATE + "/#", MOVIE_BY_RELEASE_DATE_WITH_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_VOTES, MOVIE_BY_VOTES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_VOTES + "/#", MOVIE_BY_VOTES_WITH_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVORITE_MOVIE, FAVORITE_MOVIE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_FAVORITE_MOVIE + "/#", FAVORITE_MOVIE_WITH_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_DETAILS, MOVIE_DETAILS);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_REVIEW, MOVIE_REVIEW);

        return uriMatcher;
    }
}

