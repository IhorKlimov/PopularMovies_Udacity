package com.example.igorklimov.popularmoviesdemo;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.example.igorklimov.popularmoviesdemo.helper.RecyclerViewPositionHelper;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static java.util.Arrays.asList;

public class MoviesGridFragment extends Fragment {
    private ArrayList<Movie> moviesList;
    private RecyclerView recyclerView;
    private String JsonResponse;
    private CustomAdapter customAdapter;
    private int page = 1;
    static boolean sortChanged = false;

    public MoviesGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
//asdfasdf

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "AS");
        if (sortChanged) {
            page = 1;
            moviesList.clear();
            sortChanged = false;
            recyclerView.addOnScrollListener(new ScrollListener());
            new FetchAsyncTask().execute();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);
        moviesList = new ArrayList<>();
        customAdapter = new CustomAdapter(moviesList, getActivity());
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), (
                getActivity()
                        .getResources()
                        .getConfiguration()
                        .orientation) == Configuration.ORIENTATION_PORTRAIT ? 2 : 3
        ));

        recyclerView.addOnScrollListener(new ScrollListener());

        new FetchAsyncTask().execute();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent settings = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settings);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", moviesList);
        super.onSaveInstanceState(outState);
    }

    private class FetchAsyncTask extends AsyncTask<Void, Void, Movie[]> {
        private final static String IMAGE_BASE = "http://image.tmdb.org/t/p";
        private final static String W_185 = "/w185/";
        private final static String DISCOVER_MOVIES = "http://api.themoviedb.org/3/discover/movie";
        private final static String API_KEY = "&api_key=";
        private final static String SORT_BY = "?sort_by=";
        private final static String PAGE = "&page=";
        private final static String POPULARITY_DESC = "popularity.desc";
        private final static String RELEASE_DATE_DESC = "release_date.desc&vote_count.gte=10&vote_average.gte=7&release_date.lte=";
        private final static String VOTE_AVG_DESC = "vote_average.desc&vote_count.gte=1000";

        @Override
        protected Movie[] doInBackground(Void... params) {
            Log.d("TAG", "Getting more data");
            HttpURLConnection connection = null;
            InputStream input = null;
            BufferedReader reader = null;

            try {
                String sortType = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(getString(R.string.key_sort_types), "");
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

    private class ScrollListener extends RecyclerView.OnScrollListener {
        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = 6; // The minimum amount of items to have below your current scroll position before loading more.
        int firstVisibleItem, visibleItemCount, totalItemCount;

        RecyclerViewPositionHelper mRecyclerViewHelper;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mRecyclerViewHelper.getItemCount();
            firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                new FetchAsyncTask().execute();
                loading = true;
            }
        }

    }

}


