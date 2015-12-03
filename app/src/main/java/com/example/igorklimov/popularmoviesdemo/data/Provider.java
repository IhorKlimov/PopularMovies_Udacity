package com.example.igorklimov.popularmoviesdemo.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByPopularity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByReleaseDate;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByVotes;

import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.CONTENT_AUTHORITY;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_MOVIE_BY_POPULARITY;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_MOVIE_BY_RELEASE_DATE;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_MOVIE_BY_VOTES;

/**
 * Created by Igor Klimov on 11/29/2015.
 */
public class Provider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    //    private static final SQLiteQueryBuilder queryBuilder;
    private MoviesDbHelper moviesDbHelper;
    private ContentResolver contentResolver;

    private static final int MOVIE_BY_POPULARITY = 100;
    private static final int MOVIE_BY_POPULARITY_WITH_ID = 101;
    private static final int MOVIE_BY_RELEASE_DATE = 200;
    private static final int MOVIE_BY_RELEASE_DATE_WITH_ID = 201;
    private static final int MOVIE_BY_VOTES = 300;
    private static final int MOVIE_BY_VOTES_WITH_ID = 301;

//    static {
//        queryBuilder = new SQLiteQueryBuilder();
//        queryBuilder.setTables(MovieContract.MovieByPopularity.TABLE_NAME);
//    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        if (getContext() != null) {
            contentResolver = getContext().getContentResolver();
        }
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
        SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
        Cursor cursor;
        String id;
        switch (uriMatcher.match(uri)) {
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
                id = MovieByPopularity.getIdFromUri(uri);
                cursor = db.query(MovieByReleaseDate.TABLE_NAME, projection,
                        MovieByPopularity._ID + "=?", new String[]{id}, null, null, sortOrder);
                break;
            case MOVIE_BY_VOTES:
                cursor = db.query(MovieByVotes.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_BY_VOTES_WITH_ID:
                id = MovieByPopularity.getIdFromUri(uri);
                cursor = db.query(MovieByVotes.TABLE_NAME, projection,
                        MovieByPopularity._ID + "=?", new String[]{id}, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        cursor.setNotificationUri(contentResolver, uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d("TAG", "insert: ");
        SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        Uri result;
        long insert;
        switch (uriMatcher.match(uri)) {
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
            default:
                throw new UnsupportedOperationException();
        }
        contentResolver.notifyChange(uri, null);
        return result;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int inserted = 0;
        SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
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
            default:
                throw new UnsupportedOperationException();
        }

        if (inserted != 0) contentResolver.notifyChange(uri, null);
        return inserted;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        int deleted;
        String id;
        switch (uriMatcher.match(uri)) {
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
                id = MovieByPopularity.getIdFromUri(uri);
                deleted = db.delete(MovieByReleaseDate.TABLE_NAME, MovieByPopularity._ID + "=?", new String[]{id});
                break;
            case MOVIE_BY_VOTES:
                deleted = db.delete(MovieByVotes.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_BY_VOTES_WITH_ID:
                id = MovieByPopularity.getIdFromUri(uri);
                deleted = db.delete(MovieByVotes.TABLE_NAME, MovieByPopularity._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException();
        }
        if (deleted > 0) contentResolver.notifyChange(uri, null);

        return deleted;
    }

    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_POPULARITY, MOVIE_BY_POPULARITY);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_POPULARITY + "/#", MOVIE_BY_POPULARITY_WITH_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_RELEASE_DATE, MOVIE_BY_RELEASE_DATE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_RELEASE_DATE + "/#", MOVIE_BY_RELEASE_DATE_WITH_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_VOTES, MOVIE_BY_VOTES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE_BY_VOTES + "/#", MOVIE_BY_VOTES_WITH_ID);

        return uriMatcher;
    }
}

