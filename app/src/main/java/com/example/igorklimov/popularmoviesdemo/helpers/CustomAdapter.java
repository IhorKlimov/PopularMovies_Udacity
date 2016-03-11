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

package com.example.igorklimov.popularmoviesdemo.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByPopularity;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByReleaseDate;
import com.example.igorklimov.popularmoviesdemo.data.MovieContract.MovieByVotes;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Igor Klimov on 11/7/2015.
 */
public class CustomAdapter extends CursorRecyclerViewAdapter<CustomAdapter.ViewHolder> {
    private static final String LOG_TAG = "CustomAdapter";
    private static final String TAG = "CustomAdapter";
    public static int sPrevious = -1;

    private Context mContext;
    private final int mOrientation;
    private int mMinWidth = 0;
    private int mMinHeight = 0;
    private RecyclerView mRecyclerView;
    private MoviesAdapterOnClickHandler mHandler;

    public CustomAdapter(Context context, Cursor c, RecyclerView recyclerView,
                         MoviesAdapterOnClickHandler handler) {
        super(c);
        mContext = context;
        mOrientation = context.getResources().getConfiguration().orientation;
        mRecyclerView = recyclerView;
        mHandler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        ImageView posterImageView = (ImageView) view.findViewById(R.id.poster);
        boolean isTablet = Utility.isTabletPreference(mContext);
        if (mMinWidth == 0) {
            if (isTablet && mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                mMinHeight = mRecyclerView.getHeight();
                mMinWidth = (int) (((double) mMinHeight / 278) * 185);
            } else {
                mMinWidth = mRecyclerView.getWidth()
                        / (mOrientation == Configuration.ORIENTATION_LANDSCAPE
                        || isTablet ? 3 : 2);
                mMinHeight = (int) (((double) mMinWidth / 185) * 278);
            }
        }
        posterImageView.setMinimumWidth(mMinWidth);
        posterImageView.setMinimumHeight(mMinHeight);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor, int position) {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.id = cursor.getInt(0);

        Picasso.with(mContext)
                .load(Utility.getPoster(cursor))
                .noFade()
                .into(viewHolder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });
        ViewCompat.setTransitionName(viewHolder.imageView, "iconView" + position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView imageView;
        final ProgressBar progressBar;
        int id;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.poster);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
            if (!Utility.isTabletPreference(mContext) || getAdapterPosition() != sPrevious) {
                Uri movieUri = null;
                switch (Utility.getSortByPreference(mContext)) {
                    case 1:
                        movieUri = MovieByPopularity.buildMovieUri(id);
                        break;
                    case 2:
                        movieUri = MovieByReleaseDate.buildMovieUri(id);
                        break;
                    case 3:
                        movieUri = MovieByVotes.buildMovieUri(id);
                        break;
                    case 4:
                        movieUri = MovieContract.FavoriteMovie.buildMovieUri(id);
                        break;
                }
                Log.d("TAG", "onClick: " + movieUri);
                mHandler.onClick(movieUri, this);
                sPrevious = getAdapterPosition();
            }
        }
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(Uri uri, ViewHolder holder);
    }
}
