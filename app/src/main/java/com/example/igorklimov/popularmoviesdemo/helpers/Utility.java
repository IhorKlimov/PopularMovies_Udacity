package com.example.igorklimov.popularmoviesdemo.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.MainActivity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByPopularity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByReleaseDate;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByVotes;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Igor Klimov on 11/27/2015.
 */
public class Utility {

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

    public static JSONObject[] getJsonMovies(String jsonResponse) {
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

    public static String getVote(Cursor c) {
        return c.getString(c.getColumnIndex(MovieContract.COLUMN_AVERAGE_VOTE));
    }

    public static String getPlot(Cursor c) {
        return c.getString(c.getColumnIndex(MovieContract.COLUMN_PLOT));
    }

    public static long getRowCountPreference(Context c) {
        String sortBy = getSortByPreference(c);
        switch (sortBy) {
            case "1":
                return PreferenceManager.getDefaultSharedPreferences(c).getLong(c.getString(R.string.pop_row_count), 0);
            case "2":
                return PreferenceManager.getDefaultSharedPreferences(c).getLong(c.getString(R.string.release_row_count), 0);
            default:
                return PreferenceManager.getDefaultSharedPreferences(c).getLong(c.getString(R.string.votes_row_count), 0);
        }
    }

    public static void updateRowCountPreference(Context c) {
        Cursor query = c.getContentResolver().query(MovieByPopularity.CONTENT_URI, null, null, null, null);
        if (query != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putLong(c.getString(R.string.pop_row_count), (
                    PreferenceManager.getDefaultSharedPreferences(c).getLong(c.getString(R.string.pop_row_count), 0)
                            + query.getCount())).apply();
            query.close();
        }
        query = c.getContentResolver().query(MovieByReleaseDate.CONTENT_URI, null, null, null, null);
        if (query != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putLong(c.getString(R.string.release_row_count), (
                    PreferenceManager.getDefaultSharedPreferences(c).getLong(c.getString(R.string.release_row_count), 0)
                            + query.getCount())).apply();
            query.close();
        }
        query = c.getContentResolver().query(MovieByVotes.CONTENT_URI, null, null, null, null);
        if (query != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putLong(c.getString(R.string.votes_row_count), (
                    PreferenceManager.getDefaultSharedPreferences(c).getLong(c.getString(R.string.votes_row_count), 0)
                            + query.getCount())).apply();
            query.close();
        }
    }

    public static String getSortByPreference(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(c.getString(R.string.key_sort_types), "0");
    }

    public static Uri getContentUri(Context c) {
        Uri contentUri;
        switch (getSortByPreference(c)) {
            case "1":
                contentUri = MovieByPopularity.CONTENT_URI;
                break;
            case "2":
                contentUri = MovieByReleaseDate.CONTENT_URI;
                break;
            case "3":
                contentUri = MovieByVotes.CONTENT_URI;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return contentUri;
    }

    public static int getPagePreference(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        switch (getSortByPreference(c)) {
            case "1":
                return prefs.getInt(c.getString(R.string.pop_page), 1);
            case "2":
                return prefs.getInt(c.getString(R.string.release_page), 1);
            default:
                return prefs.getInt(c.getString(R.string.votes_page), 1);
        }
    }

    public static void incrementPage(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        switch (getSortByPreference(c)) {
            case "1":
                prefs.edit().putInt(c.getString(R.string.pop_page), (getPagePreference(c) + 1)).apply();
                break;
            case "2":
                prefs.edit().putInt(c.getString(R.string.release_page), (getPagePreference(c) + 1)).apply();
                break;
            default:
                prefs.edit().putInt(c.getString(R.string.votes_page), (getPagePreference(c) + 1)).apply();
        }
    }

    public static void setIsTwoPanePreference(Context c, boolean b) {
        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(c.getString(R.string.is_two_pane), b).apply();
    }

    public static boolean isTwoPanePreference(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(c.getString(R.string.is_two_pane), false);
    }

}


