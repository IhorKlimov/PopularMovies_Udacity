package com.example.igorklimov.popularmoviesdemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.fragments.DetailFragment;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.details_fragment);
        if (!Utility.isTabletPreference(this) && Utility.getSortByPreference(this) == 4) {
            if (df.toRemove) Utility.removeFromFavorite(df.cursor, this);
        }
    }
}
