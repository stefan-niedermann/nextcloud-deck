/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package it.niedermann.android.tablayouthelper;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

/**
 * This is a fork of the android-tablayouthelper project to make it compatible with Viewpager2.
 * See also https://github.com/h6ah4i/android-tablayouthelper/issues/13
 */
public class TabLayoutHelper {
    private TabLayout mTabLayout;
    private TabTitleGenerator mTabTitleGenerator;
    private ViewPager2 mViewPager;

    private TabLayout.OnTabSelectedListener mInternalOnTabSelectedListener;
    private FixedTabLayoutOnPageChangeListener mInternalTabLayoutOnPageChangeListener;
    private RecyclerView.AdapterDataObserver mInternalDataSetObserver;
    private Runnable mAdjustTabModeRunnable;
    private Runnable mSetTabsFromPagerAdapterRunnable;
    private Runnable mUpdateScrollPositionRunnable;
    private boolean mAutoAdjustTabMode = false;
    private boolean mDuringSetTabsFromPagerAdapter;

    /**
     * Constructor.
     *
     * @param tabLayout         TabLayout instance
     * @param viewPager         ViewPager2 instance
     * @param tabTitleGenerator TabTitleGenerator instance
     */
    public TabLayoutHelper(@NonNull TabLayout tabLayout, @NonNull ViewPager2 viewPager, @NonNull TabTitleGenerator tabTitleGenerator) {
        RecyclerView.Adapter adapter = viewPager.getAdapter();

        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }

        mTabLayout = tabLayout;
        mViewPager = viewPager;
        mTabTitleGenerator = tabTitleGenerator;


        mInternalDataSetObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                handleOnDataSetChanged();
            }
        };

        mInternalOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleOnTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        };

        mInternalTabLayoutOnPageChangeListener = new FixedTabLayoutOnPageChangeListener(mTabLayout);


        viewPager.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                setTabsFromPagerAdapter(mTabLayout, viewPager.getAdapter(), viewPager.getCurrentItem());
            }
        });

        setupWithViewPager(mTabLayout, mViewPager);

        setAutoAdjustTabModeEnabled(true);
    }

    /**
     * Sets auto tab mode adjustment enabled
     *
     * @param enabled True for enabled, otherwise false.
     */
    @SuppressWarnings("WeakerAccess")
    public void setAutoAdjustTabModeEnabled(boolean enabled) {
        if (mAutoAdjustTabMode == enabled) {
            return;
        }
        mAutoAdjustTabMode = enabled;

        if (mAutoAdjustTabMode) {
            adjustTabMode(-1);
        } else {
            cancelPendingAdjustTabMode();
        }
    }

    /**
     * Unregister internal listener objects, release object references, etc.
     * This method should be called in order to avoid memory leaks.
     */
    public void release() {
        cancelPendingAdjustTabMode();
        cancelPendingSetTabsFromPagerAdapter();
        cancelPendingUpdateScrollPosition();

        if (mInternalDataSetObserver != null) {
            mInternalDataSetObserver = null;
        }
        if (mInternalOnTabSelectedListener != null) {
            mTabLayout.removeOnTabSelectedListener(mInternalOnTabSelectedListener);
            mInternalOnTabSelectedListener = null;
        }
        if (mInternalTabLayoutOnPageChangeListener != null) {
            mInternalTabLayoutOnPageChangeListener = null;
        }
        mViewPager = null;
        mTabLayout = null;
    }

    /**
     * Override this method if you want to use custom tab layout.
     *
     * @param tabLayout TabLayout
     * @param position  Position of the item
     * @return TabLayout.Tab
     */
    private TabLayout.Tab onCreateTab(TabLayout tabLayout, int position) {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(mTabTitleGenerator.getTitle(position));
        return tab;
    }

    public void setTabTitleGenerator(TabTitleGenerator mTabTitleGenerator) {
        this.mTabTitleGenerator = mTabTitleGenerator;
    }

    /**
     * Override this method if you want to use custom tab layout
     *
     * @param tab Tab
     */
    private void onUpdateTab(TabLayout.Tab tab) {
        if (tab.getCustomView() == null) {
            tab.setCustomView(null); // invokes update() method internally.
        }
    }

    //
    // internal methods
    //
    private void handleOnDataSetChanged() {
        cancelPendingUpdateScrollPosition();
        cancelPendingSetTabsFromPagerAdapter();

        if (mSetTabsFromPagerAdapterRunnable == null) {
            mSetTabsFromPagerAdapterRunnable = () -> setTabsFromPagerAdapter(mTabLayout, mViewPager.getAdapter(), mViewPager.getCurrentItem());
        }

        mTabLayout.post(mSetTabsFromPagerAdapterRunnable);
    }

    private void handleOnTabSelected(TabLayout.Tab tab) {
        if (mDuringSetTabsFromPagerAdapter) {
            return;
        }
        mViewPager.setCurrentItem(tab.getPosition());
        cancelPendingUpdateScrollPosition();
    }

    private void cancelPendingAdjustTabMode() {
        if (mAdjustTabModeRunnable != null) {
            mTabLayout.removeCallbacks(mAdjustTabModeRunnable);
            mAdjustTabModeRunnable = null;
        }
    }

    private void cancelPendingSetTabsFromPagerAdapter() {
        if (mSetTabsFromPagerAdapterRunnable != null) {
            mTabLayout.removeCallbacks(mSetTabsFromPagerAdapterRunnable);
            mSetTabsFromPagerAdapterRunnable = null;
        }
    }

    private void cancelPendingUpdateScrollPosition() {
        if (mUpdateScrollPositionRunnable != null) {
            mTabLayout.removeCallbacks(mUpdateScrollPositionRunnable);
            mUpdateScrollPositionRunnable = null;
        }
    }

    private void adjustTabMode(final int prevScrollX) {
        final int prevScrollXMinZero = prevScrollX < 0 ? mTabLayout.getScrollX() : prevScrollX;

        if (mAdjustTabModeRunnable != null) {
            return;
        }

        if (ViewCompat.isLaidOut(mTabLayout)) {
            adjustTabModeInternal(mTabLayout, prevScrollXMinZero);
        } else {
            mAdjustTabModeRunnable = () -> {
                mAdjustTabModeRunnable = null;
                adjustTabModeInternal(mTabLayout, prevScrollXMinZero);
            };
            mTabLayout.post(mAdjustTabModeRunnable);
        }
    }

    private TabLayout.Tab createNewTab(TabLayout tabLayout, int position) {
        return onCreateTab(tabLayout, position);
    }

    private void setupWithViewPager(@NonNull TabLayout tabLayout, @NonNull ViewPager2 viewPager) {
        final RecyclerView.Adapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }

        setTabsFromPagerAdapter(tabLayout, adapter, viewPager.getCurrentItem());
        viewPager.getAdapter().registerAdapterDataObserver(mInternalDataSetObserver);
        viewPager.registerOnPageChangeCallback(mInternalTabLayoutOnPageChangeListener);
        tabLayout.addOnTabSelectedListener(mInternalOnTabSelectedListener);
    }

    private void setTabsFromPagerAdapter(@NonNull TabLayout tabLayout, @Nullable RecyclerView.Adapter adapter, final int currentItem) {
        try {
            mDuringSetTabsFromPagerAdapter = true;

            int prevScrollX = tabLayout.getScrollX();

            // remove all tabs
            tabLayout.removeAllTabs();

            // add tabs
            if (adapter != null) {
                int count = adapter.getItemCount();
                for (int i = 0; i < count; i++) {
                    TabLayout.Tab tab = createNewTab(tabLayout, i);
                    tabLayout.addTab(tab, false);
                    updateTab(tab);
                }

                // select current tab
                final int currentItemPosition = Math.min(currentItem, count - 1);
                TabLayout.Tab tab = tabLayout.getTabAt(currentItemPosition);
                if (currentItemPosition >= 0 && tab != null) {
                    tab.select();
                }
            }

            // adjust tab mode & gravity
            if (mAutoAdjustTabMode) {
                adjustTabMode(prevScrollX);
            } else {
                // restore scroll position if needed
                int curTabMode = tabLayout.getTabMode();
                if (curTabMode == TabLayout.MODE_SCROLLABLE) {
                    tabLayout.scrollTo(prevScrollX, 0);
                }
            }
        } finally {
            mDuringSetTabsFromPagerAdapter = false;
        }
    }

    private void updateTab(TabLayout.Tab tab) {
        onUpdateTab(tab);
    }

    private int determineTabMode(@NonNull TabLayout tabLayout) {
        LinearLayout slidingTabStrip = (LinearLayout) tabLayout.getChildAt(0);

        int childCount = slidingTabStrip.getChildCount();

        // NOTE: slidingTabStrip.getMeasuredWidth() method does not return correct width!
        // Need to measure each tabs and calculate the sum of them.

        final int tabLayoutWidth;
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            tabLayoutWidth = tabLayout.getMeasuredWidth() - tabLayout.getPaddingStart() - tabLayout.getPaddingEnd();
        } else {
            tabLayoutWidth = tabLayout.getMeasuredWidth() - tabLayout.getPaddingLeft() - tabLayout.getPaddingRight();
        }
        final int tabLayoutHeight = tabLayout.getMeasuredHeight() - tabLayout.getPaddingTop() - tabLayout.getPaddingBottom();

        if (childCount == 0) {
            return TabLayout.MODE_FIXED;
        }

        int stripWidth = 0;
        int maxWidthTab = 0;
        int tabHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(tabLayoutHeight, View.MeasureSpec.EXACTLY);

        for (int i = 0; i < childCount; i++) {
            View tabView = slidingTabStrip.getChildAt(i);
            tabView.measure(View.MeasureSpec.UNSPECIFIED, tabHeightMeasureSpec);
            int tabWidth = tabView.getMeasuredWidth();
            stripWidth += tabWidth;
            maxWidthTab = Math.max(maxWidthTab, tabWidth);
        }

        return ((stripWidth < tabLayoutWidth) && (maxWidthTab < (tabLayoutWidth / childCount)))
                ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE;
    }

    private void adjustTabModeInternal(@NonNull TabLayout tabLayout, int prevScrollX) {
        int prevTabMode = tabLayout.getTabMode();

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        int newTabMode = determineTabMode(tabLayout);

        cancelPendingUpdateScrollPosition();

        if (newTabMode == TabLayout.MODE_FIXED) {
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            LinearLayout slidingTabStrip = (LinearLayout) tabLayout.getChildAt(0);
            slidingTabStrip.setGravity(Gravity.CENTER_HORIZONTAL);
            if (prevTabMode == TabLayout.MODE_SCROLLABLE) {
                // restore scroll position
                tabLayout.scrollTo(prevScrollX, 0);
            } else {
                // scroll to current selected tab
                mUpdateScrollPositionRunnable = () -> {
                    mUpdateScrollPositionRunnable = null;
                    updateScrollPosition();
                };
                mTabLayout.post(mUpdateScrollPositionRunnable);
            }
        }
    }

    private void updateScrollPosition() {
        mTabLayout.setScrollPosition(mTabLayout.getSelectedTabPosition(), 0, false);
    }
}
