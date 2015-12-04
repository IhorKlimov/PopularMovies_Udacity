package com.example.igorklimov.popularmoviesdemo.fragments;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.MainActivity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.helpers.CustomAdapter;
import com.example.igorklimov.popularmoviesdemo.helpers.ScrollListener;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;

public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private ScrollListener listener;
    private static final int LOADER = 1;
    private MainActivity mainActivity;

    public MoviesGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void sortChanged() {
        getLoaderManager().restartLoader(LOADER, null, this);
        if (Utility.isTwoPanePreference(getContext())) selectFirstItem();
        listener.refresh();
        recyclerView.smoothScrollToPosition(0);
    }

    private void selectFirstItem() {
//            recyclerView.setItemChecked(0, true);
        mainActivity.onItemClick(MovieContract.MovieByPopularity
                .buildMovieUri(Utility.getRowCountPreference(getActivity()) + 1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movies_grid, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);
        customAdapter = new CustomAdapter(getActivity(), null, recyclerView);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), (
                getActivity()
                        .getResources()
                        .getConfiguration()
                        .orientation) == Configuration.ORIENTATION_PORTRAIT ? 2 : 3
        ));
//        Cursor cursor = getContext().getContentResolver()
//                .query(MovieContract.MovieByPopularity.CONTENT_URI, null, null, null, null);
//        if (cursor.getCount() == 0) {
            SyncAdapter.syncImmediately(getActivity());
//        }
//        cursor.close();

        listener = new ScrollListener(getActivity());
        recyclerView.addOnScrollListener(listener);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER, null, this);
        mainActivity = (MainActivity) getContext();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri contentUri = Utility.getContentUri(getContext());
        return new CursorLoader(getActivity(), contentUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        customAdapter.swapCursor(cursor);
        if (Utility.isTwoPanePreference(getContext())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectFirstItem();
                }
            },300);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        customAdapter.swapCursor(null);
    }

}


