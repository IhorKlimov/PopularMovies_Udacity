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

package com.example.igorklimov.popularmoviesdemo.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.igorklimov.popularmoviesdemo.sync.SyncAdapter;

/**
 * Created by Igor Klimov on 11/15/2015.
 */
public class ScrollListener extends RecyclerView.OnScrollListener {
    private int mPreviousTotal = 0; // The total number of items in the data set after the last load
    private boolean mLoading = true; // True if we are still waiting for the last set of data to load.
    private int mVisibleThreshold = 6; // The minimum amount of items to have below your current scroll position before loading more.
    private Context mContext;
    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;

    public ScrollListener(Context context) {
        mContext = context;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (Utility.getSortByPreference(mContext)!=4) {
            RecyclerViewPositionHelper helper = RecyclerViewPositionHelper.createHelper(recyclerView);
            mVisibleItemCount = recyclerView.getChildCount();
            mTotalItemCount = helper.getItemCount();
            mFirstVisibleItem = helper.findFirstVisibleItemPosition();
            if (mLoading && mTotalItemCount > mPreviousTotal) {
                    mLoading = false;
                    mPreviousTotal = mTotalItemCount;
            }
            if (!mLoading && (mTotalItemCount - mVisibleItemCount) <= (mFirstVisibleItem + mVisibleThreshold)) {
                Utility.incrementPage(mContext);
                SyncAdapter.syncImmediately(mContext);
                mLoading = true;
            }
        }
    }

    public void refresh() {
        mPreviousTotal = 0;
        mLoading = true;
        mVisibleThreshold = 6;
        mFirstVisibleItem = 0;
        mVisibleItemCount = 0;
        mTotalItemCount = 0;
    }

}
