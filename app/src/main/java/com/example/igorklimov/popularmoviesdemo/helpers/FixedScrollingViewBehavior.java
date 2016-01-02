package com.example.igorklimov.popularmoviesdemo.helpers;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

public class FixedScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {

    private static final String TAG = "FixedScrollingView";

    public FixedScrollingViewBehavior() {
    }

    public FixedScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec,
                                  int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (child.getLayoutParams().height == -1) {
            List dependencies = parent.getDependencies(child);
            if (dependencies.isEmpty()) {
                Log.v(TAG, "onMeasureChild:1 ");
                return false;
            }

            AppBarLayout appBar = findFirstAppBarLayout(dependencies);
            if (appBar != null && ViewCompat.isLaidOut(appBar)) {
                if (ViewCompat.getFitsSystemWindows(appBar)) {
                    ViewCompat.setFitsSystemWindows(child, true);
                }

                int scrollRange = appBar.getTotalScrollRange();
                int height = parent.getHeight() - appBar.getMeasuredHeight() + Math.min(scrollRange,
                        parent.getHeight() - heightUsed);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height,
                        View.MeasureSpec.EXACTLY);
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed,
                        heightMeasureSpec, heightUsed);
                Log.v(TAG, "onMeasureChild:2 ");
                return true;
            }
        }

        Log.v(TAG, "onMeasureChild:3 ");
        return false;
    }

    private static AppBarLayout findFirstAppBarLayout(List<View> views) {
        int i = 0;

        for (int z = views.size(); i < z; ++i) {
            View view = views.get(i);
            if (view instanceof AppBarLayout) {
                Log.v(TAG, "onMeasureChild:4 ");
                return (AppBarLayout) view;
            }
        }
        Log.v(TAG, "onMeasureChild:5 ");
        return null;
    }
}

