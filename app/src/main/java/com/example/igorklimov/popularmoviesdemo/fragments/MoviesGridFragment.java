package com.example.igorklimov.popularmoviesdemo.fragments;

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


import com.example.igorklimov.popularmoviesdemo.helpers.CustomAdapter;
import com.example.igorklimov.popularmoviesdemo.helpers.ScrollListener;
import com.example.igorklimov.popularmoviesdemo.model.FetchAsyncTask;
import com.example.igorklimov.popularmoviesdemo.model.Movie;
import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.SettingsActivity;
import com.example.igorklimov.popularmoviesdemo.helpers.RecyclerViewPositionHelper;

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
    private CustomAdapter customAdapter;
    public static boolean sortChanged = false;
    private ScrollListener listener;

    public MoviesGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sortChanged) {
            FetchAsyncTask.page = 1;
            moviesList.clear();
            sortChanged = false;
            listener.refresh();
            new FetchAsyncTask(getActivity(), moviesList, customAdapter).execute();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);
        if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            Log.d("TAG", "Found saved array");
            moviesList = savedInstanceState.getParcelableArrayList("movies");
        } else {
            moviesList = new ArrayList<>();
        }

        customAdapter = new CustomAdapter(moviesList, getActivity());
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), (
                getActivity()
                        .getResources()
                        .getConfiguration()
                        .orientation) == Configuration.ORIENTATION_PORTRAIT ? 2 : 3
        ));

        listener = new ScrollListener(getActivity(), moviesList, customAdapter);
        recyclerView.addOnScrollListener(listener);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            new FetchAsyncTask(getActivity(), moviesList, customAdapter).execute();
        }

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

}


