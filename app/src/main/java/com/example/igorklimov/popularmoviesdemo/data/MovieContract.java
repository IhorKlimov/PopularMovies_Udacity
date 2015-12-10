package com.example.igorklimov.popularmoviesdemo.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

/**
 * Created by Igor Klimov on 11/28/2015.
 */
public final class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.igorklimov.popularmoviesdemo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE_BY_POPULARITY = "movie_by_popularity";
    public static final String PATH_MOVIE_BY_RELEASE_DATE = "movie_by_release_date";
    public static final String PATH_MOVIE_BY_VOTES = "movie_by_votes";
    public static final String PATH_FAVORITE_MOVIE = "favorite_movie";

    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_POSTER = "poster";
    public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_GENRES = "genres";
    public static final String COLUMN_AVERAGE_VOTE = "average_vote";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_BUDGET = "budget";
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_PLOT = "plot";

    private static final String TEXT_TYPE = " TEXT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMA_SEP = ",";

    private MovieContract() {

    }

    public static abstract class MovieByPopularity implements BaseColumns {
        public static final String TABLE_NAME = "movie_by_popularity";

        public static final String SQL_CREATE_ENTRIES = createEntries(TABLE_NAME);

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_BY_POPULARITY).build();

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static abstract class MovieByReleaseDate implements BaseColumns {
        public static final String TABLE_NAME = "movie_by_release_date";

        public static final String SQL_CREATE_ENTRIES = createEntries(TABLE_NAME);

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_BY_RELEASE_DATE).build();

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static abstract class MovieByVotes implements BaseColumns {
        public static final String TABLE_NAME = "movie_by_votes";

        public static final String SQL_CREATE_ENTRIES = createEntries(TABLE_NAME);

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_BY_VOTES).build();

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static abstract class FavoriteMovie implements BaseColumns {
        public static final String TABLE_NAME = "favorite_movie";

        public static final String SQL_CREATE_ENTRIES = createEntries(TABLE_NAME);

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIE).build();

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    @NonNull
    private static String createEntries(String tableName) {
        return "CREATE TABLE " + tableName + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + TEXT_TYPE + NOT_NULL + COMA_SEP
                + COLUMN_POSTER + TEXT_TYPE + NOT_NULL + COMA_SEP
                + COLUMN_RELEASE_DATE + TEXT_TYPE + COMA_SEP
                + COLUMN_GENRES + TEXT_TYPE + COMA_SEP
                + COLUMN_AVERAGE_VOTE + TEXT_TYPE + COMA_SEP
                + COLUMN_PLOT + TEXT_TYPE + COMA_SEP
                + COLUMN_MOVIE_ID + TEXT_TYPE + COMA_SEP
                + COLUMN_BACKDROP_PATH + TEXT_TYPE + COMA_SEP
                + COLUMN_BUDGET + TEXT_TYPE + COMA_SEP
                + COLUMN_LENGTH + TEXT_TYPE + ");";
    }

}
