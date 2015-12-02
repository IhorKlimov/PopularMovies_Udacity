package com.example.igorklimov.popularmoviesdemo.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.fragments.DetailFragment;
import com.example.igorklimov.popularmoviesdemo.fragments.MoviesGridFragment;
import com.example.igorklimov.popularmoviesdemo.model.Movie;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(getApplication(), R.xml.pref_general, false);
        SyncAdapter.initializeSyncAdapter(this);
        twoPane = findViewById(R.id.details_fragment) != null;
        Log.d("TAG", twoPane + "");
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public void onItemClick(Movie movie) {
        if (twoPane) {
            int height = findViewById(R.id.details_fragment).getHeight();
            DetailFragment fragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("movie", movie);
            bundle.putInt("fragmentHeight", height);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.details_fragment, fragment);
            transaction.commit();
        } else {
            Intent getDetails = new Intent(this, DetailActivity.class);
            getDetails.putExtra("movie", movie);
            this.startActivity(getDetails);
        }
    }
}
