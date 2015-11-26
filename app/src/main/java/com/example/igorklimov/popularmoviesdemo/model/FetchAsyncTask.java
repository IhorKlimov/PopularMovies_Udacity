package com.example.igorklimov.popularmoviesdemo.model;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.helpers.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;

/**
 * Created by Igor Klimov on 11/15/2015.
 */
public class FetchAsyncTask extends AsyncTask<Void, Void, Movie[]> {

    private final static String IMAGE_BASE = "http://image.tmdb.org/t/p";
    private final static String W_185 = "/w185/";
    private final static String DISCOVER_MOVIES = "http://api.themoviedb.org/3/discover/movie";
    private final static String API_KEY = "&api_key=daa8e62fb35a4e6821d58725b5abb88f";
    private final static String SORT_BY = "?sort_by=";
    private final static String PAGE = "&page=";
    private final static String POPULARITY_DESC = "popularity.desc";
    private final static String RELEASE_DATE_DESC = "release_date.desc&vote_count.gte=10&vote_average.gte=7&release_date.lte=";
    private final static String VOTE_AVG_DESC = "vote_average.desc&vote_count.gte=1000";

    public static int page = 1;
    private Context context;
    private String JsonResponse;
    private List<Movie> moviesList;
    private CustomAdapter customAdapter;

    public FetchAsyncTask(Context context, List<Movie> moviesList, CustomAdapter customAdapter) {
        this.context = context;
        this.moviesList = moviesList;
        this.customAdapter = customAdapter;
    }


    @Override
    protected Movie[] doInBackground(Void... params) {
        Log.d("TAG", "Getting more data");
        HttpURLConnection connection = null;
        InputStream input = null;
        BufferedReader reader = null;

        try {
            String sortType = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(context.getString(R.string.key_sort_types), "");
            switch (sortType) {
                case "1":
                    sortType = POPULARITY_DESC;
                    break;
                case "2":
                    String twoWeeksAhead = new SimpleDateFormat("yyyyMMdd", Locale.US)
                            .format(new Date(System.currentTimeMillis() + 1_296_000_000));
                    sortType = RELEASE_DATE_DESC + twoWeeksAhead;
                    break;
                case "3":
                    sortType = VOTE_AVG_DESC;
                    break;
            }

            Log.d("TAG", DISCOVER_MOVIES + SORT_BY + sortType + API_KEY);
            Log.d("TAG",  " " + page);

            connection = (HttpURLConnection) new URL(DISCOVER_MOVIES + SORT_BY + sortType + PAGE + page + API_KEY).openConnection();
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

        Movie[] movies = new Movie[20];

        try {
            JSONObject[] JsonMovies = getJsonMovies(JsonResponse);
            for (int i = 0; i < JsonMovies.length; i++) {
                String poster_path = IMAGE_BASE + W_185 + JsonMovies[i].getString("poster_path");
                String title = JsonMovies[i].getString("title");
                String releaseDate = JsonMovies[i].getString("release_date");
                String vote = JsonMovies[i].getString("vote_average");
                String plot = JsonMovies[i].getString("overview");

                movies[i] = new Movie(poster_path, title, releaseDate, vote, plot);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    private JSONObject[] getJsonMovies(String jsonResponse) {
        JSONObject[] jsonObjects = null;
        try {
            JSONObject jObj = new JSONObject(jsonResponse);
            JSONArray results = jObj.getJSONArray("results");
            jsonObjects = new JSONObject[20];

            for (int j = 0; j < 20; j++) {
                jsonObjects[j] = results.getJSONObject(j);
            }

        } catch (JSONException e) {
            Log.e("TAG", "JSON ERROR ");
            e.printStackTrace();
        }

        return jsonObjects;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);

        moviesList.addAll(asList(movies));
        customAdapter.notifyDataSetChanged();
        page++;
    }

}