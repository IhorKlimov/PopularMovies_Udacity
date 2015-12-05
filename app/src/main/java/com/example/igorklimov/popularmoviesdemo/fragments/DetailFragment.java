package com.example.igorklimov.popularmoviesdemo.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor cursor;
    private View rootView;
    private static int fragmentHeight;

    private static final SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.US);

    private static final int DETAIL_LOADER = 300;
    private ImageView posterView;
    private TextView titleView;
    private TextView releaseDateView;
    private TextView voteView;
    private TextView plotView;

    private TextView genresView;
    private FloatingActionButton fab;

    //todo Add director, cast
    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        posterView = (ImageView) rootView.findViewById(R.id.details_poster);
        titleView = (TextView) rootView.findViewById(R.id.title);
        releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        voteView = (TextView) rootView.findViewById(R.id.vote);
        plotView = (TextView) rootView.findViewById(R.id.plot);
        genresView = (TextView) rootView.findViewById(R.id.genres);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        final ScrollView scroll = (ScrollView) rootView.findViewById(R.id.scrollView);
        final int heightPixels = getContext().getResources().getDisplayMetrics().heightPixels;
        if (actionBar != null && !Utility.isTwoPanePreference(getContext())) {
            scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Log.d("TAG", "onScrollChanged: " + scroll.getY() + " " + scroll.getScrollY());
                    if (scroll.getScrollY() >= heightPixels / 14) {
                        actionBar.hide();
                    } else if (scroll.getScrollY() < heightPixels / 14) {
                        actionBar.show();
                    }
                }
            });
        }

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fab.isActivated()) {
                    Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.star_on);
                    Utility.addToFavorite(cursor, getContext());
                    fab.setActivated(true);
                } else {
                    Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    Utility.removeFromFavorite(cursor, getContext());
                    fab.setImageResource(R.drawable.star_off);
                    fab.setActivated(false);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = getActivity().getIntent().getData();
        if (movieUri != null) {
            fragmentHeight = this.getResources().getDisplayMetrics().heightPixels;
            return new CursorLoader(getActivity(), movieUri, null, null, null, null);
        } else {
            Bundle arguments = getArguments();
            movieUri = arguments.getParcelable("movie");
            fragmentHeight = arguments.getInt("fragmentHeight");
            return new CursorLoader(getActivity(), movieUri, null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int minHeight = fragmentHeight / 3;
        int minWidth = (int) (((double) minHeight / 278) * 185);

        posterView.setMinimumWidth(minWidth);
        posterView.setMinimumHeight(minHeight);
        if (data.moveToFirst()) {
            cursor = data;
            if (Utility.isFavorite(data, getContext())) {
                fab.setImageResource(R.drawable.star_on);
                fab.setActivated(true);
            }
            Picasso.with(getActivity())
                    .load(Utility.getPoster(data))
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
            titleView.setText(Utility.getTitle(data));
            try {
                releaseDateView.setText(monthYearFormat
                        .format(initialFormat.parse(Utility.getReleaseDate(data))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            genresView.setText(Utility.getGenres(data));
            voteView.setText(String.format(getString(R.string.format_average_vote), Utility.getVote(data)));
            plotView.setText(Utility.getPlot(data));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void sortChanged() {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }
}
