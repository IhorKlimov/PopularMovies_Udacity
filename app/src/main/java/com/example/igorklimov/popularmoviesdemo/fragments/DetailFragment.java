package com.example.igorklimov.popularmoviesdemo.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igorklimov.popularmoviesdemo.BuildConfig;
import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.DetailActivity;
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

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.igorklimov.popularmoviesdemo.BuildConfig.YOUTUBE_API_KEY;
import static com.example.igorklimov.popularmoviesdemo.R.id.author;
import static com.example.igorklimov.popularmoviesdemo.R.id.group_title;
import static com.example.igorklimov.popularmoviesdemo.R.id.review_text;
import static com.example.igorklimov.popularmoviesdemo.R.layout.child;
import static com.example.igorklimov.popularmoviesdemo.R.layout.group;
import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.getJsonResponse;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String trailerUri;
    public Cursor cursor;

    private static final SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.US);
    private static final int DETAIL_LOADER = 300;

    private ImageView posterView;
    private TextView titleView;
    private TextView releaseDateView;
    private TextView voteView;
    private TextView plotView;
    private TextView genresView;
    public FloatingActionButton fab;
    private TextView length;
    private TextView budget;
    private boolean done = false;
    public boolean toRemove = false;
    private boolean inserted = false;
    private ImageView back;
    private View progressBar;
    private ImageButton playButton;
    private int defaultHeight;
    private List<Map<String, String>> childGroupForFirstGroupRow;
    private Context context;
    private ShareActionProvider actionProvider;

    //todo Add director, cast
    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        if (Utility.isTabletPreference(context)) {
            actionProvider = new ShareActionProvider(getActivity()) {
                @Override
                public View onCreateActionView() {
                    return null;
                }
            };
            item.setIcon(R.drawable.ic_share_24dp);
        } else {
            actionProvider = new ShareActionProvider(getActivity());
        }
        MenuItemCompat.setActionProvider(item, actionProvider);
        if (trailerUri != null) actionProvider.setShareIntent(createShareIntent());

    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "#Popular Movies app https://www.youtube.com/watch?v=" + trailerUri);
        return intent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        posterView = (ImageView) rootView.findViewById(R.id.details_poster);
        titleView = (TextView) rootView.findViewById(R.id.title);
        releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        voteView = (TextView) rootView.findViewById(R.id.vote);
        plotView = (TextView) rootView.findViewById(R.id.plot);
        genresView = (TextView) rootView.findViewById(R.id.genres);
        length = (TextView) rootView.findViewById(R.id.length);
        budget = (TextView) rootView.findViewById(R.id.budget);
        ExpandableListView reviews = (ExpandableListView) rootView.findViewById(R.id.reviews);
        context = getActivity();

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
                context,
                groupData,
                group,
                new String[]{"ROOT_NAME"},
                new int[]{group_title},

                listOfChildGroups,
                child,
                new String[]{"CHILD_TITLE", "CHILD_TEXT"},
                new int[]{author, review_text}
        ));

        final ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();

        final ScrollView scroll = (ScrollView) rootView.findViewById(R.id.scrollView);
        final int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        if (actionBar != null && !Utility.isTabletPreference(context)) {
            scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (scroll.getScrollY() >= heightPixels / 14) {
                        actionBar.hide();
                    } else if (scroll.getScrollY() < heightPixels / 14) {
                        actionBar.show();
                    }
                }
            });
        }

        reviews.setDividerHeight(0);
        reviews.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                if (parent.getLayoutParams().height > defaultHeight) {
                    final int x = scroll.getScrollX();
                    final int y = scroll.getScrollY() + heightPixels / 4;
                    Log.d("TAG", "onGroupClick: " + x + " " + y);
                    scroll.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll.smoothScrollTo(x, y);
                        }
                    });
                }
                return false;
            }
        });

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fab.isActivated()) {
                    if (!inserted) {
                        Toast.makeText(context, "Added to Favorites", LENGTH_SHORT).show();
                        fab.setImageResource(R.drawable.star_on);
                        if (Utility.isTabletPreference(context)
                                || Utility.getSortByPreference(context) != 4) {
                            Utility.addToFavorite(cursor, context);
                        } else {
                            toRemove = false;
                        }
                        fab.setActivated(true);
                        inserted = true;
                    }
                } else {
                    if (inserted) {
                        Toast.makeText(context, "Removed from Favorites", LENGTH_SHORT).show();
                        fab.setImageResource(R.drawable.star_off);
                        fab.setActivated(false);

                        if (Utility.isTabletPreference(context) || Utility.getSortByPreference(context) != 4) {
                            Utility.removeFromFavorite(cursor, context);
                            MoviesGridFragment.id = Utility.getId(context);
                            if (Utility.isTabletPreference(context)
                                    && Utility.getSortByPreference(context) == 4) {
                                MainActivity activity = (MainActivity) context;
                                activity.showDetails(MovieContract.FavoriteMovie.buildMovieUri(MoviesGridFragment.id));
                            }
                        } else {
                            toRemove = true;
                        }
                        inserted = false;
                    }
                }
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trailerUri != null) {
                    Log.d("TAG", "onClick: " + trailerUri);
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                            YOUTUBE_API_KEY, trailerUri);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = systemService.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            NoInternet noInternet = new NoInternet();
            noInternet.setTargetFragment(this, 2);
            if (Utility.isTabletPreference(context)) {
                noInternet.show(((MainActivity) context).getFragmentManager(), "2");
            } else {
                noInternet.show(((DetailActivity) context).getFragmentManager(), "2");
            }
        } else {
            initLoader();
        }
    }

    public void initLoader() {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = getActivity().getIntent().getData();
        if (movieUri != null) {
            return new CursorLoader(context, movieUri, null, null, null, null);
        } else {
            Bundle arguments = getArguments();
            movieUri = arguments.getParcelable("movie");
            return new CursorLoader(context, movieUri, null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!done) {
            if (data.moveToFirst()) cursor = data;
            load();
        }
    }

    public void load() {
        int fragmentWidth;
        int fragmentHeight;
        if (!Utility.isTabletPreference(context)) {
            fragmentHeight = this.getResources().getDisplayMetrics().heightPixels;
            fragmentWidth = this.getResources().getDisplayMetrics().widthPixels;
        } else {
            Bundle arguments = getArguments();
            fragmentHeight = arguments.getInt("fragmentHeight");
            fragmentWidth = arguments.getInt("fragmentWidth");
        }
        int minHeight = fragmentHeight / 3;
        int minWidth = (int) (((double) minHeight / 278) * 185);
        int backdropHeight = (int) (((double) fragmentWidth / 500) * 281);

        posterView.setMinimumWidth(minWidth);
        posterView.setMinimumHeight(minHeight);
        back.setMinimumHeight(backdropHeight);
        progressBar.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);

        new Task().execute(cursor.getString(7));
        if (Utility.isFavorite(cursor, context)) {
            fab.setImageResource(R.drawable.star_on);
            fab.setActivated(true);
            inserted = true;
        }
        Picasso.with(context)
                .load(Utility.getPoster(cursor))
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
        Picasso.with(context)
                .load(Utility.getBackdrop(cursor))
                .resize(fragmentWidth, backdropHeight)
                .into(back);
        titleView.setText(Utility.getTitle(cursor));
        try {
            releaseDateView.setText(monthYearFormat
                    .format(initialFormat.parse(Utility.getReleaseDate(cursor))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        genresView.setText(Utility.getGenres(cursor));
        voteView.setText(String.format(getString(R.string.format_average_vote), Utility.getVote(cursor)));
        plotView.setText(Utility.getPlot(cursor));
        done = true;
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
            budget.append(" $" + Utility.formatBudget(s[1]));
            trailerUri = s[2];
            if (actionProvider != null) {
                actionProvider.setShareIntent(createShareIntent());
            }
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
                totalHeight *= 2;
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

