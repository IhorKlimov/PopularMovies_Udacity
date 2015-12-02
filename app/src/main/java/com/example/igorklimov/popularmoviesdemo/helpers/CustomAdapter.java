package com.example.igorklimov.popularmoviesdemo.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
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

    public CustomAdapter(Context context, Cursor c, RecyclerView recyclerView) {
        super(context, c);
        CustomAdapter.context = context;
        orientation = context.getResources().getConfiguration().orientation;
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        ImageView posterImageView = (ImageView) view.findViewById(R.id.poster);
        if (minWidth == 0) {
            minWidth = recyclerView.getWidth()
                    / (orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
            minHeight = (int) (((double) minWidth / 185) * 278);
        }

        posterImageView.setMinimumWidth(minWidth);
        posterImageView.setMinimumHeight(minHeight);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        viewHolder.progressBar.setVisibility(View.VISIBLE);
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
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.poster);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
            MainActivity mainActivity = (MainActivity) CustomAdapter.context;
            long extra = Utility.getRowCountPreference(context);
            Uri movieUri = MovieContract.MovieEntry.buildMovieUri(getAdapterPosition() + 1 + extra);
            Log.d("TAG", "onClick: " + movieUri);
            mainActivity.onItemClick(movieUri);
        }
    }
}
