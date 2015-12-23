package com.example.igorklimov.popularmoviesdemo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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
    public static ScrollListener listener;
    private static final int LOADER = 1;
    private MainActivity mainActivity;
    private View rootView;
    public static int id = 0;
    private FragmentActivity activity;
    private boolean mHoldForTransition;

    public MoviesGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void sortChanged() {
        getLoaderManager().restartLoader(LOADER, null, this);
        listener.refresh();
        recyclerView.smoothScrollToPosition(0);
    }

    private void selectFirstItem(CustomAdapter.ViewHolder holder) {
        int sortByPreference = Utility.getSortByPreference(activity);
        Uri movieUri;
        if (sortByPreference == 1) {
            movieUri = MovieContract.MovieByPopularity.buildMovieUri(id);
        } else if (sortByPreference == 2) {
            movieUri = MovieContract.MovieByReleaseDate.buildMovieUri(id);
        } else if (sortByPreference == 3) {
            movieUri = MovieContract.MovieByVotes.buildMovieUri(id);
        } else {
            movieUri = MovieContract.FavoriteMovie.buildMovieUri(id);
        }
        CustomAdapter.previous = 0;
        mainActivity.onItemClick(movieUri, holder);
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.MoviesGridFragment, 0, 0);
        mHoldForTransition =
                a.getBoolean(R.styleable.MoviesGridFragment_sharedElementTransitions, false);
        a.recycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.movies_grid, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);
        activity = getActivity();
        customAdapter = new CustomAdapter(activity, null, recyclerView,
                new CustomAdapter.MoviesAdapterOnClickHandler() {
            @Override
            public void onClick(Uri uri, CustomAdapter.ViewHolder holder) {
                mainActivity.onItemClick(uri, holder);
            }
        });
        recyclerView.setAdapter(customAdapter);

        int orientation = activity
                .getResources()
                .getConfiguration()
                .orientation;
        int spanCount;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE || Utility.isTabletPreference(activity)) {
            spanCount = 3;
        } else {
            spanCount = 2;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(activity, spanCount));
        SyncAdapter.syncImmediately(activity);

        listener = new ScrollListener(getActivity());
        recyclerView.addOnScrollListener(listener);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER, null, this);
        mainActivity = (MainActivity) activity;
        if (mHoldForTransition) {
            getActivity().supportPostponeEnterTransition();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri contentUri = Utility.getContentUri(activity);
        return new CursorLoader(getActivity(), contentUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> cursorLoader, final Cursor cursor) {
        customAdapter.swapCursor(cursor);
        if (Utility.getSortByPreference(activity) == 4 && cursor.getCount() == 0) {
            rootView.findViewById(R.id.list_empty).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.list_empty).setVisibility(View.GONE);
        }
        if (mHoldForTransition) {
            getActivity().supportStartPostponedEnterTransition();
        }
        if (Utility.isTabletPreference(activity)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (id == 0) {
                        if (cursor.getCount() == 0) {
                            mainActivity.showDetails(null);
                        } else {
                            id = Utility.getId(activity);
                            CustomAdapter.ViewHolder vh =
                                    (CustomAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(0);
                            selectFirstItem(vh);
                        }
                    }
                }
            }, 0);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        customAdapter.swapCursor(null);
    }

}


