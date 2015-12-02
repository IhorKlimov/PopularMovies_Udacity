package com.example.igorklimov.popularmoviesdemo.helpers;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;

/**
 * Created by Igor Klimov on 11/15/2015.
 */
public class ScrollListener extends RecyclerView.OnScrollListener {
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 6; // The minimum amount of items to have below your current scroll position before loading more.
    private Context context;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    public ScrollListener(Context context, CustomAdapter customAdapter) {
        this.context = context;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        RecyclerViewPositionHelper helper = RecyclerViewPositionHelper.createHelper(recyclerView);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = helper.getItemCount();
        firstVisibleItem = helper.findFirstVisibleItemPosition();
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            Log.d("TAG", "onScrolled: RUNNING syncImmediately");
//            Cursor cursor = context.getContentResolver()
//                    .query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
//            if (!(cursor.getCount() < SyncAdapter.page * 20)) {
//                SyncAdapter.setPage(cursor.getCount() / 20 + 1);
//                Log.d("TAG", "onScrolled: SETTING PAGE ---------->>>> TO " + SyncAdapter.page);
//            }
//            cursor.close();
            SyncAdapter.syncImmediately(context);
            loading = true;
        }
    }

    public void refresh() {
        previousTotal = 0;
        loading = true;
        visibleThreshold = 6;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
    }

}
