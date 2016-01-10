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

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.MainActivity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.helpers.CustomAdapter;
import com.example.igorklimov.popularmoviesdemo.helpers.ScrollListener;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;
import static com.example.igorklimov.popularmoviesdemo.helpers.CustomAdapter.MoviesAdapterOnClickHandler;

public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MoviesGridFragment";
    private static final int LOADER = 1;
    public static ScrollListener sListener;
    public static int sId = 0;

    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdapter;
    private MainActivity mMainActivity;
    private boolean mHoldForTransition;
    private boolean mIsTablet;
    private TextView mEmptyMessage;

    public MoviesGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void sortChanged() {
        getLoaderManager().restartLoader(LOADER, null, this);
        sListener.refresh();
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void selectFirstItem(CustomAdapter.ViewHolder holder) {
        int sortByPreference = Utility.getSortByPreference(mMainActivity);
        Uri movieUri;
        if (sortByPreference == 1) {
            movieUri = MovieContract.MovieByPopularity.buildMovieUri(sId);
        } else if (sortByPreference == 2) {
            movieUri = MovieContract.MovieByReleaseDate.buildMovieUri(sId);
        } else if (sortByPreference == 3) {
            movieUri = MovieContract.MovieByVotes.buildMovieUri(sId);
        } else {
            movieUri = MovieContract.FavoriteMovie.buildMovieUri(sId);
        }
        CustomAdapter.sPrevious = 0;
        mMainActivity.onItemClick(movieUri, holder);
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.MoviesGridFragment, 0, 0);
        mHoldForTransition =
                a.getBoolean(R.styleable.MoviesGridFragment_sharedElementTransitions, false);
        mIsTablet = !mHoldForTransition;
        a.recycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movies_grid, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);
        mEmptyMessage = (TextView) rootView.findViewById(R.id.list_empty);
        mMainActivity = (MainActivity) getActivity();
        mCustomAdapter = new CustomAdapter(mMainActivity, null, mRecyclerView, new MoviesAdapterOnClickHandler() {
            @Override
            public void onClick(Uri uri, CustomAdapter.ViewHolder holder) {
                mMainActivity.onItemClick(uri, holder);
            }
        });
        mRecyclerView.setAdapter(mCustomAdapter);

        setupMinSizes();

        SyncAdapter.syncImmediately(mMainActivity);

        sListener = new ScrollListener(getActivity());
        mRecyclerView.addOnScrollListener(sListener);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER, null, this);
        if (mHoldForTransition) {
            getActivity().supportPostponeEnterTransition();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri contentUri = Utility.getContentUri(mMainActivity);
        return new CursorLoader(getActivity(), contentUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> cursorLoader, final Cursor cursor) {
        mCustomAdapter.swapCursor(cursor);
        if (Utility.getSortByPreference(mMainActivity) == 4 && cursor.getCount() == 0) {
            mEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            mEmptyMessage.setVisibility(View.GONE);
        }
        if (mHoldForTransition) {
            getActivity().supportStartPostponedEnterTransition();
        }
        if (Utility.isTabletPreference(mMainActivity)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sId == 0) {
                        if (cursor.getCount() == 0) {
                            mMainActivity.showDetails(null);
                        } else {
                            sId = Utility.getId(mMainActivity);
                            CustomAdapter.ViewHolder vh =
                                    (CustomAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
                            selectFirstItem(vh);
                        }
                    }
                }
            }, 0);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCustomAdapter.swapCursor(null);
    }

    /**
     * This method sets minimum sizes for posters and RecyclerView's
     * {@link android.support.v7.widget.RecyclerView.LayoutManager}
     */
    private void setupMinSizes() {
        int orientation = mMainActivity
                .getResources()
                .getConfiguration()
                .orientation;
        int spanCount;

        if (orientation == Configuration.ORIENTATION_PORTRAIT && mIsTablet) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity,
                    HORIZONTAL, false));
        } else {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE || mIsTablet) {
                spanCount = 3;
            } else {
                spanCount = 2;
            }
            mRecyclerView.setLayoutManager(new GridLayoutManager(mMainActivity, spanCount));
        }
    }

}


