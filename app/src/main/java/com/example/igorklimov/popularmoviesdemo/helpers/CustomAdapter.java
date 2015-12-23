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
import com.example.igorklimov.popularmoviesdemo.activities.MainActivity;
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
    private static Context context;
    private final int orientation;
    private int minWidth = 0;
    private int minHeight = 0;
    private RecyclerView recyclerView;
    private MoviesAdapterOnClickHandler handler;
    public static int previous = -1;

    public CustomAdapter(Context context, Cursor c, RecyclerView recyclerView,
                         MoviesAdapterOnClickHandler handler) {
        super(c);
        CustomAdapter.context = context;
        orientation = context.getResources().getConfiguration().orientation;
        this.recyclerView = recyclerView;
        this.handler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        ImageView posterImageView = (ImageView) view.findViewById(R.id.poster);
        if (minWidth == 0) {
            minWidth = recyclerView.getWidth()
                    / (orientation == Configuration.ORIENTATION_LANDSCAPE
                    || Utility.isTabletPreference(context) ? 3 : 2);
            minHeight = (int) (((double) minWidth / 185) * 278);
        }
        posterImageView.setMinimumWidth(minWidth);
        posterImageView.setMinimumHeight(minHeight);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor, int position) {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.id = cursor.getInt(0);

        Picasso.with(context)
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
            if (!Utility.isTabletPreference(context) || getAdapterPosition() != previous) {
                Uri movieUri = null;
                switch (Utility.getSortByPreference(context)) {
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
                handler.onClick(movieUri, this);
                previous = getAdapterPosition();
            }
        }
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(Uri uri, ViewHolder holder);
    }

}
