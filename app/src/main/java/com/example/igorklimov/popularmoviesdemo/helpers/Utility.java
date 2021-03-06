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

package com.example.igorklimov.popularmoviesdemo.helpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.FavoriteMovie;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByPopularity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByReleaseDate;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByVotes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Igor Klimov on 11/27/2015.
 */
public class Utility {

    private static final String TAG = "Utility";

    public static String getJsonResponse(String s) {
        HttpURLConnection connection = null;
        InputStream input = null;
        BufferedReader reader = null;
        String JsonResponse = null;

        try {
            URL url = new URL(s);
            Log.d("TAG", url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            input = connection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (input != null) {
                reader = new BufferedReader(new InputStreamReader(input));
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                JsonResponse = builder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return JsonResponse;
    }

    public static JSONObject[] getJsonMovies(@NonNull String jsonResponse) {
        JSONObject[] jsonObjects = null;
        try {
            JSONObject jObj = new JSONObject(jsonResponse);
            JSONArray results = jObj.getJSONArray("results");
            jsonObjects = new JSONObject[20];

            for (int j = 0; j < 20; j++) {
                jsonObjects[j] = results.getJSONObject(j);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObjects;
    }

    public static int[] getGenres(JSONObject jsonMovie) {
        int[] genres = null;
        try {
            JSONArray jsonArray = jsonMovie.getJSONArray("genre_ids");
            int length = jsonArray.length();
            genres = new int[length];
            for (int i = 0; i < length; i++) {
                genres[i] = jsonArray.getInt(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return genres;
    }

    public static String formatGenres(int[] genres) {
        String result = "";
        for (int genreId : genres) {
            if (result.contains(",")) return result;
            switch (genreId) {
                case 28:
                    result = concat(result, "Action");
                    break;
                case 12:
                    result = concat(result, "Adventure");
                    break;
                case 16:
                    result = concat(result, "Animation");
                    break;
                case 35:
                    result = concat(result, "Comedy");
                    break;
                case 80:
                    result = concat(result, "Crime");
                    break;
                case 99:
                    result = concat(result, "Documentary");
                    break;
                case 18:
                    result = concat(result, "Drama");
                    break;
                case 10751:
                    result = concat(result, "Family");
                    break;
                case 14:
                    result = concat(result, "Fantasy");
                    break;
                case 10769:
                    result = concat(result, "Foreign");
                    break;
                case 36:
                    result = concat(result, "History");
                    break;
                case 27:
                    result = concat(result, "Horror");
                    break;
                case 10402:
                    result = concat(result, "Music");
                    break;
                case 9648:
                    result = concat(result, "Mystery");
                    break;
                case 10749:
                    result = concat(result, "Romance");
                    break;
                case 878:
                    result = concat(result, "Sci-Fi");
                    break;
                case 10770:
                    result = concat(result, "TV Movie");
                    break;
                case 53:
                    result = concat(result, "Thriller");
                    break;
                case 10752:
                    result = concat(result, "War");
                    break;
                case 37:
                    result = concat(result, "Western");
                    break;
            }
        }

        return result;
    }

    @NonNull
    public static String formatBudget(String format) {
        String result = "";
        int j = format.length();
        for (int i = j - 1; i >= 0; i--) {
            result = result.concat(format.charAt(i) + "");
            if ((j - i) % 3 == 0 && i != 0) result = result.concat(",");
        }
        format = "";
        for (int i = result.length() - 1; i >= 0; i--) {
            format = format.concat(result.charAt(i) + "");
        }
        return format;
    }

    private static String concat(String to, String concat) {
        return to.concat(to.length() == 0 ? concat : ", " + concat);
    }

    public static String getPoster(Cursor c) {
        return c.getString(c.getColumnIndex(MovieContract.COLUMN_POSTER));
    }

    public static String getGenres(Cursor c) {
        return c.getString(c.getColumnIndex(MovieContract.COLUMN_GENRES));
    }

    public static String getTitle(Cursor c) {
        return c.getString(c.getColumnIndex(MovieContract.COLUMN_TITLE));
    }

    public static String getReleaseDate(Cursor c) {
        return c.getString(c.getColumnIndex(MovieContract.COLUMN_RELEASE_DATE));
    }

    public static double getVote(Cursor c) {
        return Double.valueOf(c.getString(c.getColumnIndex(MovieContract.COLUMN_AVERAGE_VOTE)));
    }

    public static String getPlot(Cursor c) {
        return c.getString(c.getColumnIndex(MovieContract.COLUMN_PLOT));
    }

    public static int getSortByPreference(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(c.getString(R.string.key_sort_types), 1);
    }

    public static void setSortByPreference(Context c, int value) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putInt(c.getString(R.string.key_sort_types), value).apply();
    }

    public static Uri getContentUri(Context c) {
        Uri contentUri;
        switch (getSortByPreference(c)) {
            case 1:
                contentUri = MovieByPopularity.CONTENT_URI;
                break;
            case 2:
                contentUri = MovieByReleaseDate.CONTENT_URI;
                break;
            case 3:
                contentUri = MovieByVotes.CONTENT_URI;
                break;
            case 4:
                contentUri = FavoriteMovie.CONTENT_URI;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return contentUri;
    }

    public static void initializePagePreference(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        prefs.edit().putInt(c.getString(R.string.pop_page), 1).apply();
        prefs.edit().putInt(c.getString(R.string.release_page), 1).apply();
        prefs.edit().putInt(c.getString(R.string.votes_page), 1).apply();
    }

    public static int getPagePreference(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        switch (getSortByPreference(c)) {
            case 1:
                return prefs.getInt(c.getString(R.string.pop_page), 1);
            case 2:
                return prefs.getInt(c.getString(R.string.release_page), 1);
            case 3:
                return prefs.getInt(c.getString(R.string.votes_page), 1);
        }
        return -1;
    }

    public static void incrementPage(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        switch (getSortByPreference(c)) {
            case 1:
                prefs.edit().putInt(c.getString(R.string.pop_page), (getPagePreference(c) + 1)).apply();
                break;
            case 2:
                prefs.edit().putInt(c.getString(R.string.release_page), (getPagePreference(c) + 1)).apply();
                break;
            case 3:
                prefs.edit().putInt(c.getString(R.string.votes_page), (getPagePreference(c) + 1)).apply();
        }
    }

    public static void setIsTabletPreference(Context c, boolean b) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(c.getString(R.string.is_two_pane), b).apply();
    }

    public static boolean isTabletPreference(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(c.getString(R.string.is_two_pane), false);
    }

    public static boolean isFavorite(Cursor cursor, Context context) {
        String title = getTitle(cursor);
        Cursor query = context.getContentResolver().query(FavoriteMovie.CONTENT_URI, null
                , MovieContract.COLUMN_TITLE + "=?", new String[]{title}, null);
        return query.getCount() != 0;
    }

    public static Uri addToFavorite(Cursor cursor, Context context) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.COLUMN_TITLE, Utility.getTitle(cursor));
        values.put(MovieContract.COLUMN_RELEASE_DATE, Utility.getReleaseDate(cursor));
        values.put(MovieContract.COLUMN_POSTER, Utility.getPoster(cursor));
        values.put(MovieContract.COLUMN_GENRES, Utility.getGenres(cursor));
        values.put(MovieContract.COLUMN_AVERAGE_VOTE, Utility.getVote(cursor));
        values.put(MovieContract.COLUMN_PLOT, Utility.getPlot(cursor));
        values.put(MovieContract.COLUMN_MOVIE_ID, Utility.getMovieId(cursor));
        values.put(MovieContract.COLUMN_BACKDROP_PATH, Utility.getBackdrop(cursor));

        return context.getContentResolver().insert(FavoriteMovie.CONTENT_URI, values);
    }

    public static String getLength(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_LENGTH));
    }

    public static String getBudget(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_BUDGET));
    }

    private static String getMovieId(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_MOVIE_ID));
    }

    public static int removeFromFavorite(Cursor cursor, Context context) {
//        setFavoritesRowCountPreference(context, -1);
        return context.getContentResolver().delete(FavoriteMovie.CONTENT_URI,
                MovieContract.COLUMN_TITLE + "=?", new String[]{getTitle(cursor)});
    }

    public static int getId(Context context) {
        Uri uri = getContentUri(context);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        int id = 0;
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0);
            cursor.close();
        }
        return id;
    }

    public static String getBackdrop(Cursor data) {
        return data.getString(data.getColumnIndex(MovieContract.COLUMN_BACKDROP_PATH));
    }

    public static void addDetails(ContentValues details, ArrayList<ContentValues> allReviews,
                                  Context context) {
        ContentResolver resolver = context.getContentResolver();

        ContentValues[] a = new ContentValues[allReviews.size()];
        allReviews.toArray(a);

        Log.v(TAG, "addDetails: " + resolver.insert(MovieContract.Details.CONTENT_URI, details));
        Log.v(TAG, "addDetails: " + resolver.bulkInsert(MovieContract.Review.CONTENT_URI, a));
    }

    public static String getCast(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_CAST));
    }

    public static String getDirector(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_DIRECTOR));
    }

    public static String getTrailerUrl(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_TRAILER_URL));
    }

    public static String getAuthor(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_AUTHOR));
    }

    public static String getReviewText(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_REVIEW_TEXT));
    }
}


