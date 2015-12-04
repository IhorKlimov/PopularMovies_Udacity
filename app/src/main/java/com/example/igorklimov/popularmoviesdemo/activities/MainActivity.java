package com.example.igorklimov.popularmoviesdemo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.fragments.DetailFragment;
import com.example.igorklimov.popularmoviesdemo.fragments.MoviesGridFragment;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String DETAILFRAGMENT_TAG = "DETAIL_FRAGMENT";
    public static boolean sortChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(getApplication(), R.xml.pref_general, false);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.settings) {
//            Intent settings = new Intent(this, SettingsActivity.class);
//            startActivity(settings);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
