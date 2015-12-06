package com.example.igorklimov.popularmoviesdemo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.fragments.DetailFragment;
import com.example.igorklimov.popularmoviesdemo.fragments.MoviesGridFragment;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String DETAILFRAGMENT_TAG = "DETAIL_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Utility.setSortByPreference(this, 1);
        SyncAdapter.initializeSyncAdapter(this);
        Utility.setIsTwoPanePreference(this, findViewById(R.id.details_fragment) != null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(null);
    }

    private void reload() {
        MoviesGridFragment mf = (MoviesGridFragment) getSupportFragmentManager().findFragmentById(R.id.movies_view);
        if (null != mf) {
            MoviesGridFragment.id = 0;
            mf.sortChanged();
        }
        DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if (null != df) {
            df.sortChanged();
        }
    }

    public void onItemClick(Uri movieUri) {
        if (Utility.isTwoPanePreference(this)) {
            showDetails(movieUri);
        } else {
            Intent getDetails = new Intent(this, DetailActivity.class).setData(movieUri);
            startActivity(getDetails);
        }
    }

    public void showDetails(Uri movieUri) {

//        MoviesGridFragment.id = 0;
        DetailFragment fragment;
        if (movieUri == null) {
            Fragment f = getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (f != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(f);
                transaction.commit();
            }
        } else {
            int height = findViewById(R.id.details_fragment).getHeight();
            fragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("movie", movieUri);
            bundle.putInt("fragmentHeight", height);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.details_fragment, fragment, DETAILFRAGMENT_TAG);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.by_popularity:
                Utility.setSortByPreference(this, 1);
                SyncAdapter.syncImmediately(this);
                reload();
                break;
            case R.id.by_release_date:
                Utility.setSortByPreference(this, 2);
                SyncAdapter.syncImmediately(this);
                reload();
                break;
            case R.id.by_votes:
                Utility.setSortByPreference(this, 3);
                SyncAdapter.syncImmediately(this);
                reload();
                break;
            case R.id.action_favorites:
                Utility.setSortByPreference(this, 4);
                SyncAdapter.syncImmediately(this);
                reload();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
