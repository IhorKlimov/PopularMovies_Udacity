package com.example.igorklimov.popularmoviesdemo.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.igorklimov.popularmoviesdemo.model.Movie;
import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.activities.DetailActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Igor Klimov on 11/7/2015.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static ArrayList<Movie> movies;
    private static Context context;
    private int newWidth;
    private int newHeight;

    public CustomAdapter(ArrayList<Movie> movies, Context context) {
        CustomAdapter.movies = movies;
        CustomAdapter.context = context;
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int orientation = context.getResources().getConfiguration().orientation;
        newWidth = screenWidth / (orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
        newHeight = (int) (((double) newWidth / 185) * 278);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        ImageView viewById = (ImageView) view.findViewById(R.id.poster);
        viewById.setMinimumWidth(newWidth);
        viewById.setMinimumHeight(newHeight);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);

        Picasso.with(context)
                .load(movies.get(position).getPostersUrl())
                .noFade()
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return movies.size();
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
            Intent getDetails = new Intent(context, DetailActivity.class);
            getDetails.putExtra("movie", movies.get(getAdapterPosition()));
            context.startActivity(getDetails);
        }
    }
}
