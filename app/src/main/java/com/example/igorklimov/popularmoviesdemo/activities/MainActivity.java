/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.igorklimov.popularmoviesdemo.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.igorklimov.popularmoviesdemo.R;
import com.example.igorklimov.popularmoviesdemo.fragments.DetailFragment;
import com.example.igorklimov.popularmoviesdemo.fragments.MoviesGridFragment;
import com.example.igorklimov.popularmoviesdemo.helpers.CustomAdapter;
import com.example.igorklimov.popularmoviesdemo.helpers.Utility;
import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

/**
 * Master Activity, used for handsets and tablets, all item clicks
 * go through {@link #onItemClick(Uri, CustomAdapter.ViewHolder)}
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String DETAILFRAGMENT_TAG = "DETAIL_FRAGMENT";
    private DrawerLayout mDrawer;
    private int mHeight;
    private int mWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
//
        client.networkInterceptors().add(new StethoInterceptor());

        setContentView(R.layout.drawer);
        Utility.setIsTabletPreference(this, findViewById(R.id.details_fragment) != null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (savedInstanceState == null) Utility.setSortByPreference(this, 1);

        SyncAdapter.initializeSyncAdapter(this);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(null);
        Typeface typeface = Typeface
                .createFromAsset(getAssets(), "fonts/RemachineScript_Personal_Use.ttf");
        TextView title = (TextView) findViewById(R.id.app_title);
        if (title != null) title.setTypeface(typeface);

    }


    private void reload() {
        MoviesGridFragment mf = (MoviesGridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.movies_view);
        if (null != mf) {
            MoviesGridFragment.sId = 0;
            CustomAdapter.sPrevious = -1;
            mf.sortChanged();
        }
        DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if (null != df) df.sortChanged();
    }

    /**
     * This method is called every time a user clicks on a movie
     * and when first movie selected automatically, regardless of handset or tablet
     *
     * @param movieUri movieUri selected movie {@link Uri}
     * @param holder   a {@link com.example.igorklimov.popularmoviesdemo.helpers.CustomAdapter.ViewHolder}
     *                 used to get a poster transition name for transitions
     */
    public void onItemClick(Uri movieUri, CustomAdapter.ViewHolder holder) {
        if (Utility.isTabletPreference(this)) {
            showDetails(movieUri);
        } else {
            Intent getDetails = new Intent(this, DetailActivity.class).setData(movieUri);
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            new Pair<View, String>(holder.imageView,
                                    getString(R.string.detail_icon_transition_name)));
            ActivityCompat.startActivity(this, getDetails, activityOptions.toBundle());
        }
    }

    /**
     * This method is used for tablets only to display a detail view
     *
     * @param movieUri selected movie {@link Uri}
     */
    public void showDetails(Uri movieUri) {
        DetailFragment fragment;
        if (movieUri == null) {
            Fragment f = getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (f != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(f);
                transaction.commit();
            }
        } else {
            if (mHeight == 0) {
                mHeight = findViewById(R.id.details_fragment).getHeight();
                mWidth = findViewById(R.id.details_fragment).getWidth();
            }
            fragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("movie", movieUri);
            bundle.putInt("fragmentHeight", mHeight);
            bundle.putInt("fragmentWidth", mWidth);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.details_fragment, fragment, DETAILFRAGMENT_TAG);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) mDrawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
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
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
