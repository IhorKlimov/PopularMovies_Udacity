package com.example.igorklimov.popularmoviesdemo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Igor Klimov on 11/28/2015.
 */
public final class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.igorklimov.popularmoviesdemo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";


    private MovieContract() {

    }

    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_AVERAGE_VOTE = "average_vote";
        public static final String COLUMN_PLOT = "plot";

        private static final String TEXT_TYPE = " TEXT";
        private static final String NOT_NULL = " NOT NULL";
        private static final String COMA_SEP = ",";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TITLE + TEXT_TYPE + NOT_NULL + COMA_SEP
                        + COLUMN_POSTER + TEXT_TYPE + NOT_NULL + COMA_SEP
                        + COLUMN_RELEASE_DATE + TEXT_TYPE + COMA_SEP
                        + COLUMN_GENRES + TEXT_TYPE + COMA_SEP
                        + COLUMN_AVERAGE_VOTE + TEXT_TYPE + COMA_SEP
                        + COLUMN_PLOT + TEXT_TYPE + ");";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME + ";";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri) {
            String s = uri.getPathSegments().get(1);
            Log.d("TestDb", s + "   STRING");
            return s;
        }

    }

}
