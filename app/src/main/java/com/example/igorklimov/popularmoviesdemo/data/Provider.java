package com.example.igorklimov.popularmoviesdemo.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry;

import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.CONTENT_AUTHORITY;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.PATH_MOVIE;

/**
 * Created by Igor Klimov on 11/29/2015.
 */
public class Provider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder queryBuilder;
    private MoviesDbHelper moviesDbHelper;
    private ContentResolver contentResolver;

    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 101;

    static {
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieEntry.TABLE_NAME);
    }

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
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                cursor = queryBuilder.query(moviesDbHelper.getReadableDatabase(),
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
                String id = MovieEntry.getIdFromUri(uri);
                cursor = queryBuilder.query(moviesDbHelper.getReadableDatabase(), projection,
                        MovieEntry._ID + "=?", new String[]{id}, null, null, sortOrder);
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
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                long insert = db.insert(MovieEntry.TABLE_NAME, null, values);
                result = MovieEntry.buildMovieUri(insert);
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
            case MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long insert = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (insert != -1) inserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (inserted != 0) contentResolver.notifyChange(uri, null);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return inserted;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        int deleted;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                deleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
                String id = MovieEntry.getIdFromUri(uri);
                deleted = db.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException();
        }
        if (deleted >0) contentResolver.notifyChange(uri, null);

        return deleted;
    }

    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE, MOVIE);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }
}

