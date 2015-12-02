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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.MainActivity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;

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

import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry.COLUMN_AVERAGE_VOTE;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry.COLUMN_GENRES;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry.COLUMN_PLOT;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry.COLUMN_POSTER;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry.COLUMN_RELEASE_DATE;
import static com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieEntry.COLUMN_TITLE;
import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.getGenres;
import static com.example.igorklimov.popularmoviesdemo.helpers.Utility.getJsonMovies;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final int DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
    private static final int SYNC_INTERVAL = DAY_IN_MILLISECONDS;
    private static final int FLEX_TIME = SYNC_INTERVAL / 3;

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
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.US);
    private ContentResolver mContentResolver;

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
            Utility.updateRowCountPreference(context);
            int delete = mContentResolver.delete(MovieEntry.CONTENT_URI, null, null);
            prefs.edit().putLong(context.getString(R.string.last_update), System.currentTimeMillis()).apply();
            Log.d("TAG", "onPerformSync: ERASE THE DATABASE -------" + delete);
            page = 1;
            syncImmediately(context);
        } else {
            getData();
        }
    }

    private void getData() {
        HttpURLConnection connection = null;
        InputStream input = null;
        BufferedReader reader = null;

        Cursor cursor = mContentResolver.query(MovieEntry.CONTENT_URI, null, null, null, null);
        if (!(cursor.getCount() < page * 20)) page = cursor.getCount() / 20 + 1;
        Log.d("TAG", "onPerformSync: cursorCount " + cursor.getCount() + " page: " + page);
        cursor.close();
        try {
            String sortType = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(context.getString(R.string.key_sort_types), "");
            switch (sortType) {
                case "1":
                    sortType = POPULARITY_DESC;
                    break;
                case "2":
                    String twoWeeksAhead = DATE_FORMAT
                            .format(new Date(System.currentTimeMillis() + 1_296_000_000));
                    sortType = RELEASE_DATE_DESC + twoWeeksAhead;
                    break;
                case "3":
                    sortType = VOTE_AVG_DESC;
                    break;
            }

            URL url = new URL(DISCOVER_MOVIES
                    + SORT_BY + sortType + PAGE + page + API_KEY);
            Log.d("TAG", "Getting more data from server");
            Log.d("TAG", url.toString());
            connection = (HttpURLConnection) url.openConnection();
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

        try {
            JSONObject[] JsonMovies = getJsonMovies(JsonResponse);
            Log.d("TAG", "INSERTING EXTRA DATA");
            page++;
            ArrayList<ContentValues> arrayOfValues = new ArrayList<>();
            for (JSONObject jsonMovie : JsonMovies) {
                String poster = IMAGE_BASE + W_185 + jsonMovie.getString("poster_path");
                String title = jsonMovie.getString("title");
                String releaseDate = jsonMovie.getString("release_date");
                String vote = jsonMovie.getString("vote_average");
                String plot = jsonMovie.getString("overview");
                String genres = Utility.formatGenres(getGenres(jsonMovie));

                ContentValues values = new ContentValues();
                values.put(COLUMN_TITLE, title);
                values.put(COLUMN_POSTER, poster);
                values.put(COLUMN_RELEASE_DATE, releaseDate);
                values.put(COLUMN_GENRES, genres);
                values.put(COLUMN_AVERAGE_VOTE, vote);
                values.put(COLUMN_PLOT, plot);

                arrayOfValues.add(values);
            }
            ContentValues[] contentValues = arrayOfValues.toArray(new ContentValues[arrayOfValues.size()]);
            int bulkInsert = mContentResolver.bulkInsert(MovieEntry.CONTENT_URI, contentValues);
            Log.d("TAG", "onPerformSync: bulkInsert " + bulkInsert);
        } catch (JSONException e) {
            e.printStackTrace();
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
        preferences.edit().putLong(context.getString(R.string.row_count), 0).apply();

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
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}