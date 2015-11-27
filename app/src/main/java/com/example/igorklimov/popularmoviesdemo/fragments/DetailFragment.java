package com.example.igorklimov.popularmoviesdemo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;
import com.example.igorklimov.popularmoviesdemo.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private View rootView;
    private int fragmentHeight;

    private static final SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.US);

    //todo Add director, cast, genre
    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ImageView posterView = (ImageView) rootView.findViewById(R.id.details_poster);
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        TextView voteView = (TextView) rootView.findViewById(R.id.vote);
        TextView plotView = (TextView) rootView.findViewById(R.id.plot);
        TextView genresView = (TextView) rootView.findViewById(R.id.genres);

        FragmentActivity activity = getActivity();
        Movie movie = null;
        if (activity.getIntent() != null && activity.getIntent().hasExtra("movie")) {
            movie = activity.getIntent().getParcelableExtra("movie");
            fragmentHeight = this.getResources().getDisplayMetrics().heightPixels;
            Log.d("TAG", fragmentHeight + "");
        } else {
            Bundle arguments = getArguments();
            if (arguments.containsKey("movie")) {
                movie = arguments.getParcelable("movie");
                fragmentHeight = arguments.getInt("fragmentHeight");
            }
        }

        int minHeight = fragmentHeight / 3;
        int minWidth = (int) (((double) minHeight / 513) * 342);

        posterView.setMinimumWidth(minWidth);
        posterView.setMinimumHeight(minHeight);

        if (movie != null) {
            Picasso.with(activity)
                    .load(movie.getPostersUrl().replace("w185", "w342"))
                    .resize(minWidth, minHeight)
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
            try {
                releaseDateView.append(monthYearFormat
                        .format(initialFormat.parse(movie.getReleaseDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            genresView.append(Utility.formatGenres(movie.getGenres()));
            voteView.append(String.format(getString(R.string.format_average_vote), movie.getVote()));
            plotView.setText(movie.getPlot());
        }

        return rootView;
    }
}
