package com.example.igorklimov.popularmoviesdemo.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
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
import static com.example.igorklimov.popularmoviesdemo.R.id.backdrop;
import static com.example.igorklimov.popularmoviesdemo.R.id.group_title;
import static com.example.igorklimov.popularmoviesdemo.R.id.review_text;
import static com.example.igorklimov.popularmoviesdemo.R.layout.child;
import static com.example.igorklimov.popularmoviesdemo.R.layout.group;
import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.getJsonResponse;
import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.isTabletPreference;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    private static final String TAG = "DetailFragment";
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
    private TextView director;
    private TextView actors;
    private String[] strings;
    private CardView card;
    private int minHeight;
    private int minWidth;
    private NestedScrollView scroll;
    private int fragmentWidth;
    private int backdropHeight;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTabletPreference(getContext())) setHasOptionsMenu(true);
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "#Popular Movies app https://www.youtube.com/watch?v=" + trailerUri);
        return intent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!isTabletPreference(context)) {
            inflater.inflate(R.menu.menu_detail, menu);
            finishCreatingMenu(menu);
        }
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
        actors = (TextView) rootView.findViewById(R.id.actors);
        director = (TextView) rootView.findViewById(R.id.director);
        ExpandableListView reviews = (ExpandableListView) rootView.findViewById(R.id.reviews);
        context = getActivity();
        back = (ImageView) rootView.findViewById(R.id.backdrop);
        progressBar = rootView.findViewById(R.id.progressBar);
        playButton = (ImageButton) rootView.findViewById(R.id.play_button);
        card = (CardView) rootView.findViewById(R.id.card_view);

        setMinSizes(rootView);

        setupReviews(reviews);

        if (savedInstanceState != null && savedInstanceState.containsKey("strings")) {
            getSavedData(savedInstanceState);
        }

        final Toolbar bar = (Toolbar) rootView.findViewById(R.id.details_toolbar);
        setupToolbar(bar);

        scroll = (NestedScrollView) rootView.findViewById(R.id.scrollView);
        final View parallaxBar = rootView.findViewById(R.id.landscape_appbar);
        if (parallaxBar != null) setupParallaxBar(bar, parallaxBar);


        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        playButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = systemService.getActiveNetworkInfo();
        if (activeNetworkInfo == null) noInternet();
        else initLoader();
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.smoothScrollTo(0, 0);
            }
        });
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
        if (!done && data.moveToFirst()) {
            cursor = data;
            load();
        }
    }

    public void load() {
        progressBar.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
        if (strings == null) new Task().execute(cursor.getString(7));
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
                        card.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        Picasso.with(context)
                .load(Utility.getBackdrop(cursor))
                .resize(fragmentWidth, backdropHeight)
                .centerCrop()
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
    public void onSaveInstanceState(Bundle outState) {
        if (strings != null) {
            ArrayList<String> author = new ArrayList<>();
            ArrayList<String> review = new ArrayList<>();
            for (int i = 0; i < childGroupForFirstGroupRow.size(); i++) {
                author.add(childGroupForFirstGroupRow.get(i).get("CHILD_TITLE"));
                review.add(childGroupForFirstGroupRow.get(i).get("CHILD_TEXT"));
            }
            outState.putStringArray("strings", strings);
            outState.putStringArrayList("author", author);
            outState.putStringArrayList("review", review);
        }
        super.onSaveInstanceState(outState);
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
            strings = new String[5];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = "n/a";
            }
            getExtraInfo(id);
            getVideos(id);
            getReviews(id);
            getCredits(id);

            return strings;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            setExtraData(s);
        }

        private void getExtraInfo(String id) {
            String JsonResponse;
            JsonResponse = getJsonResponse("http://api.themoviedb.org/3/movie/" + id +
                    "?api_key=" + BuildConfig.TBDB_API_KEY);
            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                String runtime = jsonObject.getString("runtime");
                String budget = jsonObject.getString("budget");
                if (runtime != null && !runtime.equals("0")) strings[0] = runtime + " min";
                if (budget != null && !budget.equals("0")) {
                    strings[1] = " $" + Utility.formatBudget(budget);
                }
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
                noInternet();
            }
        }

        private void getVideos(String id) {
            String JsonResponse;
            JsonResponse = getJsonResponse("http://api.themoviedb.org/3/movie/" + id +
                    "/videos?api_key=" + BuildConfig.TBDB_API_KEY);
            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 1) {
                    for (int i = 0; i < results.length(); i++) {
                        if (!results.getJSONObject(i).getString("name").contains("Teaser")) {
                            String key = results.getJSONObject(i).getString("key");
                            if (key != null) strings[2] = key;
                            break;
                        }
                    }
                } else if (results.length() == 1) {
                    String key = results.getJSONObject(0).getString("key");
                    if (key != null) strings[2] = key;
                }
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
                noInternet();
            }
        }

        private void getReviews(String id) {
            String JsonResponse;
            JsonResponse = getJsonResponse("http://api.themoviedb.org/3/movie/" + id +
                    "/reviews?api_key=" + BuildConfig.TBDB_API_KEY);
            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                final JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    HashMap<String, String> map = new HashMap<>();
                    String author = results.getJSONObject(i).getString("author");
                    String content = results.getJSONObject(i).getString("content");
                    if (author != null) map.put("CHILD_TITLE", author);
                    if (content != null) map.put("CHILD_TEXT", content);
                    childGroupForFirstGroupRow.add(map);
                }
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
                noInternet();
            }
        }

        private void getCredits(String id) {
            String JsonResponse;
            JsonResponse = getJsonResponse("http://api.themoviedb.org/3/movie/" + id +
                    "/credits?api_key=" + BuildConfig.TBDB_API_KEY);
            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                final JSONArray cast = jsonObject.getJSONArray("cast");
                String actors = "";
                int length = cast.length() > 6 ? 6 : cast.length();
                for (int i = 0; i < length; i++) {
                    actors = actors.concat(cast.getJSONObject(i).getString("name") +
                            (i < (length - 1) ? ", " : ""));
                }
                if (actors.length() > 0) strings[3] = actors;
                JSONArray crew = jsonObject.getJSONArray("crew");
                for (int i = 0; i < crew.length(); i++) {
                    JSONObject object = crew.getJSONObject(i);
                    if (object.getString("department").equals("Directing")) {
                        String name = object.getString("name");
                        if (name != null) strings[4] = name;
                        break;
                    }
                }
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
                noInternet();
            }
        }
    }

    private void noInternet() {
        final NoInternet noInternet = new NoInternet();
        noInternet.setTargetFragment(this, 2);
        if (isTabletPreference(context)) {
            noInternet.show(((MainActivity) context).getSupportFragmentManager(), "2");
        } else {
            noInternet.show(((DetailActivity) context).getSupportFragmentManager(), "2");
        }
    }

    private void setExtraData(String[] s) {
        length.append(s[0]);
        budget.append(s[1]);
        if (!s[2].equals("n/a")) trailerUri = s[2];
        actors.append(s[3]);
        director.append(s[4]);
        if (actionProvider != null) actionProvider.setShareIntent(createShareIntent());
    }

    private void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        if (defaultHeight == 0) defaultHeight = listView.getHeight();
        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null, listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
                totalHeight += defaultHeight;
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

    private void setupReviews(ExpandableListView reviews) {
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

        final int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        reviews.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                if (parent.getLayoutParams().height > defaultHeight) {
                    final int x = scroll.getScrollX();
                    final int y = scroll.getScrollY() + heightPixels / 4;
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
    }

    private void setupToolbar(Toolbar bar) {
        if (!isTabletPreference(context)) {
            ((DetailActivity) context).setSupportActionBar(bar);
            ActionBar supportActionBar = ((DetailActivity) context).getSupportActionBar();
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        } else {
            Menu menu = bar.getMenu();
            if (null != menu) menu.clear();
            bar.inflateMenu(R.menu.menu_detail);
            finishCreatingMenu(bar.getMenu());
        }
    }

    private void getSavedData(Bundle savedInstanceState) {
        strings = savedInstanceState.getStringArray("strings");
        ArrayList<String> author = savedInstanceState.getStringArrayList("author");
        ArrayList<String> review = savedInstanceState.getStringArrayList("review");

        for (int i = 0; i < author.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("CHILD_TITLE", author.get(i));
            map.put("CHILD_TEXT", review.get(i));
            childGroupForFirstGroupRow.add(map);
        }
        setExtraData(strings);
    }

    private void setMinSizes(View rootView) {
        int fragmentHeight;
        if (!isTabletPreference(context)) {
            fragmentHeight = this.getResources().getDisplayMetrics().heightPixels;
            fragmentWidth = this.getResources().getDisplayMetrics().widthPixels;
        } else {
            Bundle arguments = getArguments();
            fragmentHeight = arguments.getInt("fragmentHeight");
            fragmentWidth = arguments.getInt("fragmentWidth");
        }
        minHeight = fragmentHeight / 3;
        minWidth = (int) (((double) minHeight / 278) * 185);
        backdropHeight = (!Utility.isTabletPreference(context) && Configuration.ORIENTATION_LANDSCAPE
                == context.getResources().getConfiguration().orientation
                ? fragmentHeight - fragmentHeight / 3
                : (int) (((double) fragmentWidth / 500) * 281));

        posterView.setMinimumWidth(minWidth);
        posterView.setMinimumHeight(minHeight);
        back.setMinimumHeight(backdropHeight);
        if (!Utility.isTabletPreference(context)) {
            rootView.findViewById(R.id.frame).setMinimumHeight(minHeight + minHeight / 10);
        }
    }

    private void setupParallaxBar(final Toolbar bar, final View parallaxBar) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll.getViewTreeObserver().addOnScrollChangedListener(
                        new ViewTreeObserver.OnScrollChangedListener() {
                            int j = bar.getHeight();
                            int b = parallaxBar.getHeight();

                            @Override
                            public void onScrollChanged() {
                                int i = scroll.getScrollY();
                                float k = -parallaxBar.getTranslationY();
                                int n = -(i / 2);
                                parallaxBar.setTranslationY(n);
                                if (j + k >= b) {
                                    int i2 = -(j - (n + b));
                                    bar.setTranslationY(Math.min(0, i2));
                                }
                            }
                        });
            }
        }, 300);
    }

    private void finishCreatingMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_share);
        if (!isTabletPreference(context)) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (!fab.isActivated()) {
                    if (!inserted) {
                        Toast.makeText(context, "Added to Favorites", LENGTH_SHORT).show();
                        fab.setImageResource(R.drawable.star_on);
                        if (isTabletPreference(context)
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

                        if (isTabletPreference(context) || Utility.getSortByPreference(context) != 4) {
                            Utility.removeFromFavorite(cursor, context);
                            MoviesGridFragment.id = Utility.getId(context);
                            if (isTabletPreference(context)
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
                break;
            case R.id.play_button:
                if (trailerUri != null) {
                    Log.d("TAG", "onClick: " + trailerUri);
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                            YOUTUBE_API_KEY, trailerUri, 0, true, false);
                    startActivity(intent);
                }
                break;
        }
    }
}

