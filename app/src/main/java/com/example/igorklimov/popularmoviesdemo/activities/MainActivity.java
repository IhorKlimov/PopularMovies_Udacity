package com.example.igorklimov.popularmoviesdemo.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.fragments.DetailFragment;
import com.example.igorklimov.popularmoviesdemo.fragments.MoviesGridFragment;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String DETAILFRAGMENT_TAG = "DETAIL_FRAGMENT";
    public static boolean sortChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(getApplication(), R.xml.pref_general, false);
        SyncAdapter.initializeSyncAdapter(this);
        Utility.setIsTwoPanePreference(this, findViewById(R.id.details_fragment) != null);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sortChanged) {
            MoviesGridFragment ff = (MoviesGridFragment) getSupportFragmentManager().findFragmentById(R.id.movies_view);
            if (null != ff) {
                ff.sortChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.sortChanged();
            }
            sortChanged = false;
        }
    }

    public void onItemClick(Uri movieUri) {
        if (Utility.isTwoPanePreference(this)) {
            int height = findViewById(R.id.details_fragment).getHeight();
            DetailFragment fragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("movie", movieUri);
            bundle.putInt("fragmentHeight", height);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.details_fragment, fragment, DETAILFRAGMENT_TAG);
            transaction.commit();
        } else {
            Intent getDetails = new Intent(this, DetailActivity.class).setData(movieUri);
            startActivity(getDetails);
        }
    }
}
