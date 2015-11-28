package com.example.igorklimov.popularmoviesdemo.data;

import android.provider.BaseColumns;

/**
 * Created by Igor Klimov on 11/28/2015.
 */
public final class MovieContract {

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
        private static final String PRIMARY_KEY = " PRIMARY KEY";
        private static final String COMA_SEP = ",";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + COLUMN_TITLE + TEXT_TYPE + PRIMARY_KEY + NOT_NULL + COMA_SEP
                        + COLUMN_POSTER + TEXT_TYPE + NOT_NULL + COMA_SEP
                        + COLUMN_RELEASE_DATE + TEXT_TYPE + COMA_SEP
                        + COLUMN_GENRES + TEXT_TYPE + COMA_SEP
                        + COLUMN_AVERAGE_VOTE + TEXT_TYPE + COMA_SEP
                        + COLUMN_PLOT + TEXT_TYPE + ");";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    }

}
