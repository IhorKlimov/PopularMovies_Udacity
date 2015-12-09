package com.example.igorklimov.popularmoviesdemo.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igorklimov.popularmoviesdemo.BuildConfig;
import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.MainActivity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.getJsonResponse;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String trailerUri;
    private Cursor cursor;

    private static final SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.US);
    private static int fragmentHeight;
    private static int fragmentWidth;
    private static final int DETAIL_LOADER = 300;

    private ImageView posterView;
    private TextView titleView;
    private TextView releaseDateView;
    private TextView voteView;
    private TextView plotView;
    private View rootView;
    private TextView genresView;
    private FloatingActionButton fab;
    private TextView length;
    private TextView budget;
    private boolean done = false;
    private ImageView back;
    private View progressBar;
    private ImageButton playButton;
    private int defaultHeight;
    private ExpandableListView reviews;
    private List<Map<String, String>> childGroupForFirstGroupRow;


    //todo Add director, cast
    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        posterView = (ImageView) rootView.findViewById(R.id.details_poster);
        titleView = (TextView) rootView.findViewById(R.id.title);
        releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        voteView = (TextView) rootView.findViewById(R.id.vote);
        plotView = (TextView) rootView.findViewById(R.id.plot);
        genresView = (TextView) rootView.findViewById(R.id.genres);
        length = (TextView) rootView.findViewById(R.id.length);
        budget = (TextView) rootView.findViewById(R.id.budget);
        reviews = (ExpandableListView) rootView.findViewById(R.id.reviews);

        reviews.setDividerHeight(0);
        reviews.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });


        back = (ImageView) rootView.findViewById(R.id.backdrop);
        progressBar = rootView.findViewById(R.id.progressBar);
        playButton = (ImageButton) rootView.findViewById(R.id.play_button);

        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("ROOT_NAME", "Reviews");
            }});
        }};
        List<List<Map<String, String>>> listOfChildGroups = new ArrayList<>();

        childGroupForFirstGroupRow = new ArrayList<>();

        listOfChildGroups.add(childGroupForFirstGroupRow);
        reviews.setAdapter(new SimpleExpandableListAdapter(
                getContext(),
                groupData,
                R.layout.group,
                new String[]{"ROOT_NAME"},
                new int[]{R.id.group_title},

                listOfChildGroups,
                R.layout.child,
                new String[]{"CHILD_TITLE", "CHILD_TEXT"},
                new int[]{R.id.author, R.id.review_text}
        ));

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        final ScrollView scroll = (ScrollView) rootView.findViewById(R.id.scrollView);
        final int heightPixels = getContext().getResources().getDisplayMetrics().heightPixels;
        if (actionBar != null && !Utility.isTwoPanePreference(getContext())) {
            scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Log.d("TAG", "onScrollChanged: " + scroll.getY() + " " + scroll.getScrollY());
                    if (scroll.getScrollY() >= heightPixels / 14) {
                        actionBar.hide();
                    } else if (scroll.getScrollY() < heightPixels / 14) {
                        actionBar.show();
                    }
                }
            });
        }

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fab.isActivated()) {
                    Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.star_on);
                    Utility.addToFavorite(cursor, getContext());
                    fab.setActivated(true);
                } else {
                    Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();

                    Utility.removeFromFavorite(cursor, getContext());
                    if (Utility.getSortByPreference(getContext()) == 4) {
                        MainActivity activity = (MainActivity) getActivity();
                        MoviesGridFragment.id = Utility.getId(getContext());

                        activity.showDetails(MovieContract.FavoriteMovie.buildMovieUri(MoviesGridFragment.id));
                    }
                    fab.setImageResource(R.drawable.star_off);
                    fab.setActivated(false);
                }
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trailerUri != null) {
                    Log.d("TAG", "onClick: " + trailerUri);
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                            BuildConfig.YOUTUBE_API_KEY, trailerUri);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = getActivity().getIntent().getData();
        if (movieUri != null) {
//            fragmentHeight = this.getResources().getDisplayMetrics().heightPixels;
//            fragmentWidth = this.getResources().getDisplayMetrics().widthPixels;
            return new CursorLoader(getActivity(), movieUri, null, null, null, null);
        } else {
            Bundle arguments = getArguments();
            movieUri = arguments.getParcelable("movie");
//            fragmentHeight = arguments.getInt("fragmentHeight");
//            fragmentWidth = arguments.getInt("fragmentWidth");
            return new CursorLoader(getActivity(), movieUri, null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!done) {
            if (!Utility.isTwoPanePreference(getContext())) {
                fragmentHeight = this.getResources().getDisplayMetrics().heightPixels;
                fragmentWidth = this.getResources().getDisplayMetrics().widthPixels;
            } else {
                Bundle arguments = getArguments();
                fragmentHeight = arguments.getInt("fragmentHeight");
                fragmentWidth = arguments.getInt("fragmentWidth");
            }
            Log.d("TAG", "run: fHeight "+ fragmentHeight);
            int minHeight = fragmentHeight / 3;
            int minWidth = (int) (((double) minHeight / 278) * 185);
            int backdropHeight = (int) (((double) fragmentWidth / 500) * 281);
            Log.d("TAG", "onLoadFinished: " + backdropHeight);

            Log.d("TAG", "run: fWidth "+ minHeight);
            posterView.setMinimumWidth(minWidth);
            posterView.setMinimumHeight(minHeight);
            back.setMinimumHeight(backdropHeight);
            progressBar.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            if (data.moveToFirst()) {
                cursor = data;
                new Task().execute(cursor.getString(7));
                if (Utility.isFavorite(data, getContext())) {
                    fab.setImageResource(R.drawable.star_on);
                    fab.setActivated(true);
                }
                Picasso.with(getActivity())
                        .load(Utility.getPoster(data))
                        .resize(minWidth, minHeight)
                        .into(posterView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                Picasso.with(getActivity())
                        .load(Utility.getBackdrop(data))
                        .resize(fragmentWidth, backdropHeight)
                        .into(back);
                titleView.setText(Utility.getTitle(data));
                try {
                    releaseDateView.setText(monthYearFormat
                            .format(initialFormat.parse(Utility.getReleaseDate(data))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                genresView.setText(Utility.getGenres(data));
                voteView.setText(String.format(getString(R.string.format_average_vote), Utility.getVote(data)));
                plotView.setText(Utility.getPlot(data));
            }
            done = true;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void sortChanged() {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }


    private class Task extends AsyncTask<String, Void, String[]> {


        @Override
        protected String[] doInBackground(String... params) {
            String id = params[0];
            String JsonResponse;
            String[] strings = new String[3];

            JsonResponse = getJsonResponse("http://api.themoviedb.org/3/movie/" + id + "?api_key=" + BuildConfig.TBDB_API_KEY);

            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                strings[0] = jsonObject.getString("runtime");
                strings[1] = jsonObject.getString("budget");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonResponse = getJsonResponse("http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + BuildConfig.TBDB_API_KEY);

            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 1) {
                    for (int i = 0; i < results.length(); i++) {
                        if (!results.getJSONObject(i).getString("name").contains("Teaser")) {
                            strings[2] = results.getJSONObject(i).getString("key");
                            break;
                        }
                    }
                } else {
                    strings[2] = results.getJSONObject(0).getString("key");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonResponse = getJsonResponse("http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + BuildConfig.TBDB_API_KEY);

            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                final JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("CHILD_TITLE", results.getJSONObject(i).getString("author"));
                    map.put("CHILD_TEXT", results.getJSONObject(i).getString("content"));
                    childGroupForFirstGroupRow.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return strings;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            length.append(s[0] + " min");
            budget.append("$" + Utility.formatBudget(s[1]));
            trailerUri = s[2];
        }
    }

    private void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        if (defaultHeight == 0) defaultHeight = listView.getHeight();
        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            totalHeight += defaultHeight;
            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null, listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < defaultHeight) height = defaultHeight;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}

