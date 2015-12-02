package com.example.igorklimov.popularmoviesdemo.helpers;

import android.database.Cursor;

import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry;

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


    public static String getUrl(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER));
    }

}


