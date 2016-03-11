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

package com.example.igorklimov.popularmoviesdemo.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
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
import android.util.SparseArray;
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
import android.net.NetworkInfo;


import com.example.igorklimov.popularmoviesdemo.BuildConfig;
import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.DetailActivity;
import com.example.igorklimov.popularmoviesdemo.activities.MainActivity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.Details;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.Review;
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
import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.isTabletPreference;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    private static final String TAG = "DetailFragment";
    private static final SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.US);
    private static final int DETAIL_LOADER = 300;
    private static final String AUTHOR = "CHILD_TITLE";
    private static final String REVIEW_TEXT = "CHILD_TEXT";

    public Cursor cursor;
    private ContentResolver mResolver;
    private ShareActionProvider mActionProvider;
    Context context;

    //    Views
    public FloatingActionButton fab;
    private ImageView mPosterView;
    private ImageButton mPlayButton;
    private ImageView mBack;
    private View mProgressBar;
    private TextView mTitleView;
    private TextView mReleaseDateView;
    private TextView mVoteView;
    private TextView mPlotView;
    private TextView mGenresView;
    private TextView mLength;
    private TextView mBudget;
    private TextView mDirectorView;
    private TextView mCastView;
    private CardView mCardView;
    private NestedScrollView mScroll;
    private ExpandableListView mReviews;
    private Toolbar mBar;

    private int mDefaultHeight;
    public boolean toRemove = false;
    private boolean mDone = false;
    private boolean mInserted = false;
    private String mTrailerUri;
    private String[] mStrings;
    String title;
    List<Map<String, String>> reviewsList;

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
        intent.putExtra(Intent.EXTRA_TEXT, "#Popular Movies app https://www.youtube.com/watch?v=" + mTrailerUri);
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
        mPosterView = (ImageView) rootView.findViewById(R.id.details_poster);
        mTitleView = (TextView) rootView.findViewById(R.id.title);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        mVoteView = (TextView) rootView.findViewById(R.id.vote);
        mPlotView = (TextView) rootView.findViewById(R.id.plot);
        mGenresView = (TextView) rootView.findViewById(R.id.genres);
        mLength = (TextView) rootView.findViewById(R.id.length);
        mBudget = (TextView) rootView.findViewById(R.id.budget);
        mCastView = (TextView) rootView.findViewById(R.id.actors);
        mDirectorView = (TextView) rootView.findViewById(R.id.director);
        mReviews = (ExpandableListView) rootView.findViewById(R.id.reviews);
        context = getActivity();
        mBack = (ImageView) rootView.findViewById(R.id.backdrop);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        mPlayButton = (ImageButton) rootView.findViewById(R.id.play_button);
        mCardView = (CardView) rootView.findViewById(R.id.card_view);
        mResolver = context.getContentResolver();
        View space = rootView.findViewById(R.id.space);

        if (space != null) {
            space.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayButton.performClick();
                    mPlayButton.setPressed(true);
                }
            });
        }

        setMinSizes(space);

        setupReviews();

        mBar = (Toolbar) rootView.findViewById(R.id.details_toolbar);
        setupToolbar();

        mScroll = (NestedScrollView) rootView.findViewById(R.id.scrollView);
        final View parallaxBar = rootView.findViewById(R.id.handset_appbar);
        if (parallaxBar != null) setupParallaxBar(mBar, parallaxBar);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isInternetAvailable()) noInternetMessage();
        else initLoader();
        mScroll.post(new Runnable() {
            @Override
            public void run() {
                mScroll.smoothScrollTo(0, 0);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayButton.setOnClickListener(null);
        fab.setOnClickListener(null);
        mReviews.setOnGroupClickListener(null);
//        mScroll.getViewTreeObserver().addOnScrollChangedListener(null);
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
        if (!mDone && data.moveToFirst()) {
            cursor = data;
            title = Utility.getTitle(cursor);

            Cursor query = mResolver.query(Details.CONTENT_URI, null,
                    MovieContract.COLUMN_TITLE + "=?", new String[]{title},
                    null);
            if (query != null && query.moveToFirst()) {
                getSavedData(query);
                query.close();
            }
            load();
        }
    }

    public void load() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
        if (mStrings == null) new Task().execute(cursor.getString(7));
        if (Utility.isFavorite(cursor, context)) {
            fab.setImageResource(R.drawable.star_on);
            fab.setActivated(true);
            mInserted = true;
        }
        Picasso.with(context).load(Utility.getPoster(cursor)).into(mPosterView, new Callback() {
            @Override
            public void onSuccess() {
                if (mProgressBar != null) mProgressBar.setVisibility(View.INVISIBLE);
                mCardView.setVisibility(View.VISIBLE);
                getActivity().supportStartPostponedEnterTransition();
            }

            @Override
            public void onError() {

            }
        });

        Picasso.with(context).load(Utility.getBackdrop(cursor)).into(mBack, new Callback() {
            @Override
            public void onSuccess() {
                mPlayButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

            }
        });
        mTitleView.setText(title);
        try {
            mReleaseDateView.setText(monthYearFormat
                    .format(initialFormat.parse(Utility.getReleaseDate(cursor))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mGenresView.setText(Utility.getGenres(cursor));
        mVoteView.setText(String.format(getString(R.string.format_average_vote), Utility.getVote(cursor)));
        mPlotView.setText(Utility.getPlot(cursor));

        mDone = true;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void sortChanged() {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }


    /**
     * An AsyncTask to get movie details
     */
    private class Task extends AsyncTask<String, Void, String[]> {
        private boolean mIsCrashed;

        @Override
        protected String[] doInBackground(String... params) {
            String id = params[0];
            mStrings = new String[5];
            for (int i = 0; i < mStrings.length; i++) {
                mStrings[i] = "n/a";
            }
            getExtraInfo(id);
            getVideos(id);
            getReviews(id);
            getCredits(id);
            if (!mIsCrashed) saveToDb();

            return mStrings;
        }


        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            Log.v(TAG, "onPostExecute: ");
            setExtraData(s);
        }

        /**
         * Get Runtime and Budget
         */
        private void getExtraInfo(String id) {
            String JsonResponse;
            JsonResponse = getJsonResponse("http://api.themoviedb.org/3/movie/" + id +
                    "?api_key=" + BuildConfig.TBDB_API_KEY);
            try {
                JSONObject jsonObject = new JSONObject(JsonResponse);
                String runtime = jsonObject.getString("runtime");
                String budget = jsonObject.getString("budget");
                if (runtime != null && !runtime.equals("0")) mStrings[0] = runtime + " min";
                if (budget != null && !budget.equals("0")) {
                    mStrings[1] = " $" + Utility.formatBudget(budget);
                }
            } catch (JSONException | NullPointerException e) {
                mIsCrashed = true;
                e.printStackTrace();
                noInternetMessage();
            }
        }

        /**
         * Get Trailer Url
         */
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
                            if (key != null) mStrings[2] = key;
                            break;
                        }
                    }
                } else if (results.length() == 1) {
                    String key = results.getJSONObject(0).getString("key");
                    if (key != null) mStrings[2] = key;
                }
            } catch (JSONException | NullPointerException e) {
                mIsCrashed = true;
                e.printStackTrace();
                noInternetMessage();
            }
        }

        /**
         * Get Reviews
         */
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
                    if (author != null) map.put(AUTHOR, author);
                    if (content != null) map.put(REVIEW_TEXT, content);
                    reviewsList.add(map);
                }
            } catch (JSONException | NullPointerException e) {
                mIsCrashed = true;
                e.printStackTrace();
                noInternetMessage();
            }
        }

        /**
         * Get Actor staff
         */
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
                if (actors.length() > 0) mStrings[3] = actors;
                JSONArray crew = jsonObject.getJSONArray("crew");
                for (int i = 0; i < crew.length(); i++) {
                    JSONObject object = crew.getJSONObject(i);
                    if (object.getString("department").equals("Directing")) {
                        String name = object.getString("name");
                        if (name != null) mStrings[4] = name;
                        break;
                    }
                }
            } catch (JSONException | NullPointerException e) {
                mIsCrashed = true;
                e.printStackTrace();
                noInternetMessage();
            }
        }

        /**
         * Save data to DB if there were no Internet connection problems
         */
        private void saveToDb() {
            Log.v(TAG, "saveToDb: ");

            ContentValues details = new ContentValues();
            ArrayList<ContentValues> allReviews = new ArrayList<>();

            details.put(MovieContract.COLUMN_TITLE, title);
            details.put(MovieContract.COLUMN_BUDGET, mStrings[1]);
            details.put(MovieContract.COLUMN_LENGTH, mStrings[0]);
            details.put(MovieContract.COLUMN_DIRECTOR, mStrings[4]);
            details.put(MovieContract.COLUMN_CAST, mStrings[3]);
            details.put(MovieContract.COLUMN_TRAILER_URL, mStrings[2]);

            for (int i = 0; i < reviewsList.size(); i++) {
                ContentValues review = new ContentValues();
                Map<String, String> map = reviewsList.get(i);

                review.put(MovieContract.COLUMN_TITLE, title);
                review.put(MovieContract.COLUMN_AUTHOR, map.get(AUTHOR));
                review.put(MovieContract.COLUMN_REVIEW_TEXT, map.get(REVIEW_TEXT));

                allReviews.add(review);
            }

            Utility.addDetails(details, allReviews, context);
        }

    }

    private void setExtraData(String[] s) {
        mLength.append(s[0]);
        mBudget.append(s[1]);
        if (!s[2].equals("n/a")) mTrailerUri = s[2];
        mCastView.append(s[3]);
        mDirectorView.append(s[4]);
        if (mActionProvider != null) mActionProvider.setShareIntent(createShareIntent());
    }

    private boolean isInternetAvailable() {
        ConnectivityManager systemService = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = systemService.getActiveNetworkInfo();
        return  activeNetworkInfo != null&& activeNetworkInfo.isConnected();
    }

    private void noInternetMessage() {
        final NoInternet noInternet = new NoInternet();
        noInternet.setTargetFragment(this, 2);
        if (isTabletPreference(context)) {
            noInternet.show(((MainActivity) context).getSupportFragmentManager(), "2");
        } else {
            noInternet.show(((DetailActivity) context).getSupportFragmentManager(), "2");
        }
    }

    private void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        if (mDefaultHeight == 0) mDefaultHeight = listView.getHeight();
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
                totalHeight += mDefaultHeight;
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < mDefaultHeight) height = mDefaultHeight;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setupReviews() {
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("ROOT_NAME", "Reviews");
            }});
        }};
        List<List<Map<String, String>>> listOfChildGroups = new ArrayList<>();

        reviewsList = new ArrayList<>();
        listOfChildGroups.add(reviewsList);

        mReviews.setAdapter(new SimpleExpandableListAdapter(
                context,
                groupData,
                group,
                new String[]{"ROOT_NAME"},
                new int[]{group_title},

                listOfChildGroups,
                child,
                new String[]{AUTHOR, REVIEW_TEXT},
                new int[]{author, review_text}
        ));

        final int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        mReviews.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                if (parent.getLayoutParams().height > mDefaultHeight) {
                    final int x = mScroll.getScrollX();
                    final int y = mScroll.getScrollY() + heightPixels / 4;
                    mScroll.post(new Runnable() {
                        @Override
                        public void run() {
                            mScroll.smoothScrollTo(x, y);
                        }
                    });
                }
                return false;
            }
        });
    }

    private void setupToolbar() {
        if (!isTabletPreference(context)) {
            ((DetailActivity) context).setSupportActionBar(mBar);
            ActionBar supportActionBar = ((DetailActivity) context).getSupportActionBar();
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
            ((DetailActivity) context).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        } else {
            Menu menu = mBar.getMenu();
            if (null != menu) menu.clear();
            mBar.inflateMenu(R.menu.menu_detail);
            finishCreatingMenu(mBar.getMenu());
        }
    }

    private void getSavedData(Cursor query) {
        mStrings = new String[5];
        ArrayList<String> author = new ArrayList<>();
        ArrayList<String> review = new ArrayList<>();

        String length = Utility.getLength(query);
        String budget = Utility.getBudget(query);
        String cast = Utility.getCast(query);
        String director = Utility.getDirector(query);
        String trailerUrl = Utility.getTrailerUrl(query);
        mStrings[0] = length;
        mStrings[1] = budget;
        mStrings[2] = trailerUrl;
        mStrings[3] = cast;
        mStrings[4] = director;

        Cursor r = mResolver.query(Review.CONTENT_URI, null, MovieContract.COLUMN_TITLE + "=?",
                new String[]{title}, null);
        if (r != null) {
            while (r.moveToNext()) {
                author.add(Utility.getAuthor(r));
                review.add(Utility.getReviewText(r));
            }
            r.close();
        }

        for (int i = 0; i < author.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(AUTHOR, author.get(i));
            map.put(REVIEW_TEXT, review.get(i));
            reviewsList.add(map);
        }
        setExtraData(mStrings);
    }

    private void setMinSizes(View space) {
        int fragmentHeight;
        int fragmentWidth;
        if (!isTabletPreference(context)) {
            fragmentHeight = this.getResources().getDisplayMetrics().heightPixels;
            fragmentWidth = this.getResources().getDisplayMetrics().widthPixels;
        } else {
            Bundle arguments = getArguments();
            fragmentHeight = arguments.getInt("fragmentHeight");
            fragmentWidth = arguments.getInt("fragmentWidth");
        }
        int minHeight = fragmentHeight / 3;
        int minWidth = (int) (((double) minHeight / 278) * 185);
        int backdropHeight = (!Utility.isTabletPreference(context) && Configuration.ORIENTATION_LANDSCAPE
                == context.getResources().getConfiguration().orientation
                ? fragmentHeight - fragmentHeight / 3
                : (int) (((double) fragmentWidth / 500) * 281));

        mPosterView.setMinimumWidth(minWidth);
        mPosterView.setMinimumHeight(minHeight);
        mBack.setMinimumHeight(backdropHeight);
        if (context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT && space != null) {
            space.setMinimumHeight(backdropHeight);
        }
    }

    private void setupParallaxBar(final Toolbar bar, final View parallaxBar) {
        final boolean isPortrait = context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScroll.getViewTreeObserver().addOnScrollChangedListener(
                        new ViewTreeObserver.OnScrollChangedListener() {
                            int j = bar.getHeight();
                            int b = parallaxBar.getHeight();

                            @Override
                            public void onScrollChanged() {
                                int i = mScroll.getScrollY();
                                float k = -parallaxBar.getTranslationY();
                                int n = -(i / 2);
                                parallaxBar.setTranslationY(n);
                                if (!isPortrait) {
                                    if (j + k >= b) {
                                        int i2 = -(j - (n + b));
                                        bar.setTranslationY(Math.min(0, i2));
                                    }
                                } else {
                                    int i2 = (b - i - j);
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
            mActionProvider = new ShareActionProvider(getActivity()) {
                @Override
                public View onCreateActionView() {
                    return null;
                }
            };
            item.setIcon(R.drawable.ic_share);
        } else {
            mActionProvider = new ShareActionProvider(getActivity());
        }
        MenuItemCompat.setActionProvider(item, mActionProvider);
        if (mTrailerUri != null) mActionProvider.setShareIntent(createShareIntent());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (!fab.isActivated()) {
                    if (!mInserted) {
                        Toast.makeText(context, "Added to Favorites", LENGTH_SHORT).show();
                        fab.setImageResource(R.drawable.star_on);
                        if (isTabletPreference(context)
                                || Utility.getSortByPreference(context) != 4) {
                            Utility.addToFavorite(cursor, context);
                        } else {
                            toRemove = false;
                        }
                        fab.setActivated(true);
                        mInserted = true;
                    }
                } else {
                    if (mInserted) {
                        Toast.makeText(context, "Removed from Favorites", LENGTH_SHORT).show();
                        fab.setImageResource(R.drawable.star_off);
                        fab.setActivated(false);

                        if (isTabletPreference(context) || Utility.getSortByPreference(context) != 4) {
                            Utility.removeFromFavorite(cursor, context);
                            MoviesGridFragment.sId = Utility.getId(context);
                            if (isTabletPreference(context)
                                    && Utility.getSortByPreference(context) == 4) {
                                MainActivity activity = (MainActivity) context;
                                activity.showDetails(MovieContract.FavoriteMovie.buildMovieUri(MoviesGridFragment.sId));
                            }
                        } else {
                            toRemove = true;
                        }
                        mInserted = false;
                    }
                }
                break;
            case R.id.play_button:
                if (mTrailerUri != null) {
                    Log.d("TAG", "onClick: " + mTrailerUri);
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                            YOUTUBE_API_KEY, mTrailerUri, 0, true, false);
                    startActivity(intent);
                }
                break;
        }
    }
}

