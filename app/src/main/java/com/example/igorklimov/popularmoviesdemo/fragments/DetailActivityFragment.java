package com.example.igorklimov.popularmoviesdemo.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igorklimov.popularmoviesdemo.model.Movie;
import com.example.igorklimov.popularmoviesdemo.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ImageView posterView = (ImageView) rootView.findViewById(R.id.details_poster);
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        TextView voteView = (TextView) rootView.findViewById(R.id.vote);
        TextView plotView = (TextView) rootView.findViewById(R.id.plot);

        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 32, getResources().getDisplayMetrics());

        FragmentActivity activity = getActivity();
        Movie movie = activity.getIntent().getParcelableExtra("movie");
        int orientation = this.getResources().getConfiguration().orientation;
        int screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = this.getResources().getDisplayMetrics().heightPixels;

        int newWidth ;
        int newHeight ;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            newWidth = screenWidth - value;
            newHeight = (int) (((double) newWidth / 342) * 513);
        } else {
            newHeight = screenHeight - value;
            newWidth = (int) (((double) newHeight / 513) * 342);
        }
        Log.d("TAG", screenWidth + " " + newWidth +" "+ newHeight);

        posterView.setMinimumWidth(newWidth);
        posterView.setMinimumHeight(newHeight);

        Picasso.with(activity)
                .load(movie.getPostersUrl().replace("w185", "w342"))
                .resize(newWidth, newHeight)
                .into(posterView, new Callback() {
                    @Override
                    public void onSuccess() {
                        rootView.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        titleView.setText(movie.getTitle());
        releaseDateView.append("Release date: " + movie.getReleaseDate().replaceAll("-", " "));
        voteView.append("Average vote: " + movie.getVote());
        plotView.setText(movie.getPlot());

        return rootView;
    }

}
