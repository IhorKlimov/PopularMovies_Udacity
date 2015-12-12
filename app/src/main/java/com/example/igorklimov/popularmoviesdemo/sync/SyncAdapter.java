package com.example.igorklimov.popularmoviesdemo.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.BuildConfig;
import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.MainActivity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByPopularity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByReleaseDate;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByVotes;
import com.example.igorklimov.popularmoviesdemo.fragments.NoInternet;
import com.example.igorklimov.popularmoviesdemo.fragments.MoviesGridFragment;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.COLUMN_AVERAGE_VOTE;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.COLUMN_GENRES;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.COLUMN_MOVIE_ID;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.COLUMN_PLOT;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.COLUMN_POSTER;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.COLUMN_RELEASE_DATE;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.COLUMN_TITLE;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.COLUMN_BACKDROP_PATH;
import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.getGenres;
import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.getJsonMovies;
import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.getJsonResponse;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final int DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
    private static final int SYNC_INTERVAL = 24 * 60 * 60;
    private static final int FLEX_TIME = SYNC_INTERVAL / 3;

    private final static String IMAGE_BASE = "http://image.tmdb.org/t/p";
    private final static String W_185 = "/w185/";
    private final static String DISCOVER_MOVIES = "http://api.themoviedb.org/3/discover/movie";
    private final static String API_KEY = "&api_key=" + BuildConfig.TBDB_API_KEY;
    private final static String SORT_BY = "?sort_by=";
    private final static String PAGE = "&page=";
    private final static String POPULARITY_DESC = "popularity.desc";
    private final static String RELEASE_DATE_DESC = "release_date.desc&vote_count.gte=10&vote_average.gte=7&release_date.lte=";
    private final static String VOTE_AVG_DESC = "vote_average.desc&vote_count.gte=1000";

    private Context context;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private ContentResolver mContentResolver;
    private ContentValues[] contentValues = new ContentValues[20];

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        this.context = context;
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    public static void syncImmediately(Context context) {
        Log.d("TAG", "syncImmediately: ");
        ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = systemService.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            new NoInternet().show(((MainActivity) context).getFragmentManager(), "1");
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long lastUpdate = prefs.getLong(context.getString(R.string.last_update), System.currentTimeMillis());
        Log.d("TAG", "onPerformSync: IF " + (System.currentTimeMillis() - lastUpdate));
        if (System.currentTimeMillis() - lastUpdate >= DAY_IN_MILLISECONDS) {
            int delete1 = mContentResolver.delete(MovieByPopularity.CONTENT_URI, null, null);
            int delete2 = mContentResolver.delete(MovieByReleaseDate.CONTENT_URI, null, null);
            int delete3 = mContentResolver.delete(MovieByVotes.CONTENT_URI, null, null);
            Log.d("TAG", "onPerformSync: ERASE THE DATABASE -------" + delete1);
            Log.d("TAG", "onPerformSync: ERASE THE DATABASE -------" + delete2);
            Log.d("TAG", "onPerformSync: ERASE THE DATABASE -------" + delete3);
            prefs.edit().putLong(context.getString(R.string.last_update), System.currentTimeMillis()).apply();
            Utility.initializePagePreference(context);
            if (MoviesGridFragment.listener != null) {
                MoviesGridFragment.listener.refresh();
            }
            syncImmediately(context);
        } else {
            getData();
        }
    }

    private void getData() {
        int sortByPreference = Utility.getSortByPreference(context);
        Uri contentUri = null;

        if (sortByPreference != 4) {
            String sortType = null;
            switch (sortByPreference) {
                case 1:
                    sortType = POPULARITY_DESC;
                    contentUri = MovieByPopularity.CONTENT_URI;
                    break;
                case 2:
                    Calendar instance = Calendar.getInstance();
                        instance.add(Calendar.YEAR, 1);
//                    String twoWeeksAhead = DATE_FORMAT
//                            .format(new Date(System.currentTimeMillis()));
                    sortType = RELEASE_DATE_DESC + instance.get(Calendar.YEAR) + "-"
                            + (instance.get(Calendar.MONTH)+ 1) + "-" + instance.get(Calendar.DAY_OF_MONTH);
                    contentUri = MovieByReleaseDate.CONTENT_URI;
                    break;
                case 3:
                    sortType = VOTE_AVG_DESC;
                    contentUri = MovieByVotes.CONTENT_URI;
                    break;
            }

            Cursor cursor = mContentResolver.query(contentUri, null, null, null, null);
            int page = Utility.getPagePreference(context);
            Log.d("TAG", "onPerformSync: cursorCount " + cursor.getCount() + " page: " + page);
            if (cursor.getCount() < page * 20) {
                String jsonResponse = getJsonResponse(DISCOVER_MOVIES + SORT_BY + sortType + PAGE + page + API_KEY);

                try {
                    JSONObject[] JsonMovies = getJsonMovies(jsonResponse);
                    Log.d("TAG", "INSERTING EXTRA DATA");
                    int i = 0;
                    for (JSONObject jsonMovie : JsonMovies) {
                        String poster = IMAGE_BASE + W_185 + jsonMovie.getString("poster_path");
                        String backdropPath = IMAGE_BASE + "/w500" + jsonMovie.getString("backdrop_path");
                        String title = jsonMovie.getString("title");
                        String releaseDate = jsonMovie.getString("release_date");
                        String vote = jsonMovie.getString("vote_average");
                        String plot = jsonMovie.getString("overview");
                        String genres = Utility.formatGenres(getGenres(jsonMovie));
                        String id = jsonMovie.getString("id");

                        ContentValues values = new ContentValues();
                        values.put(COLUMN_TITLE, title);
                        values.put(COLUMN_POSTER, poster);
                        values.put(COLUMN_RELEASE_DATE, releaseDate);
                        values.put(COLUMN_GENRES, genres);
                        values.put(COLUMN_MOVIE_ID, id);
                        values.put(COLUMN_BACKDROP_PATH, backdropPath);
                        values.put(COLUMN_AVERAGE_VOTE, vote);
                        values.put(COLUMN_PLOT, plot);

                        contentValues[i++] = values;
                    }
                    int bulkInsert = mContentResolver.bulkInsert(contentUri, contentValues);
                    Log.d("TAG", "onPerformSync: bulkInsert " + bulkInsert);
                    cursor.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account getSyncAccount(Context context) {
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.acc_sync_type));
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.d("TAG", "CREATED NEW ACCOUNT");
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, FLEX_TIME);

        ContentResolver.setSyncAutomatically(newAccount,
                context.getString(R.string.content_authority), true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putLong(context.getString(R.string.last_update), System.currentTimeMillis()).apply();

        SyncAdapter.syncImmediately(context);
    }

    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        String authority = context.getString(R.string.content_authority);
        Account account = getSyncAccount(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}