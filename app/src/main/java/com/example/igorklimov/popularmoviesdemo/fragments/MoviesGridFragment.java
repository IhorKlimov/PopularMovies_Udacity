package com.example.igorklimov.popularmoviesdemo.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.SettingsActivity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.helpers.CustomAdapter;
import com.example.igorklimov.popularmoviesdemo.helpers.ScrollListener;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;

public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
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
            sortChanged = false;
            listener.refresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movies_grid, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);
        customAdapter = new CustomAdapter(getActivity(), null, 0, recyclerView);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), (
                getActivity()
                        .getResources()
                        .getConfiguration()
                        .orientation) == Configuration.ORIENTATION_PORTRAIT ? 2 : 3
        ));
        Cursor cursor = getContext().getContentResolver()
                .query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() == 0) {
            SyncAdapter.syncImmediately(getActivity());
        }
        cursor.close();

        listener = new ScrollListener(getActivity(), customAdapter);
        recyclerView.addOnScrollListener(listener);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(1, null, this);
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        customAdapter.swapCursor(cursor);
//        if (mainActivity.mTwoPane) {
//            listView.setItemChecked(pos, true);
//            listView.smoothScrollToPosition(pos);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    listView.performItemClick(
//                            listView.getAdapter().getView(pos, null, null),
//                            pos,
//                            listView.getAdapter().getItemId(pos));
//                }
//            }, 300);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        customAdapter.swapCursor(null);
    }


//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putParcelableArrayList("movies", moviesList);
//        super.onSaveInstanceState(outState);
//    }

}


