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

package it.niedermann.nextcloud.deck.util;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class TabLayoutHelper {
    protected TabLayout mTabLayout;
    protected TabTitleGenerator mTabTitleGenerator;
    protected ViewPager2 mViewPager;

    protected TabLayout.OnTabSelectedListener mInternalOnTabSelectedListener;
    protected FixedTabLayoutOnPageChangeListener mInternalTabLayoutOnPageChangeListener;
    //    protected ViewPager2.OnAdapterChangeListener mInternalOnAdapterChangeListener;
    protected RecyclerView.AdapterDataObserver mInternalDataSetObserver;
    protected Runnable mAdjustTabModeRunnable;
    protected Runnable mSetTabsFromPagerAdapterRunnable;
    protected Runnable mUpdateScrollPositionRunnable;
    protected boolean mAutoAdjustTabMode = false;
    protected boolean mDuringSetTabsFromPagerAdapter;

    /**
     * Constructor.
     *
     * @param tabLayout         TabLayout instance
     * @param viewPager         ViewPager2 instance
     * @param tabTitleGenerator
     */
    public TabLayoutHelper(@NonNull TabLayout tabLayout, @NonNull ViewPager2 viewPager, TabTitleGenerator tabTitleGenerator) {
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
                handleOnTabUnselected(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                handleOnTabReselected(tab);
            }
        };

        mInternalTabLayoutOnPageChangeListener = new FixedTabLayoutOnPageChangeListener(mTabLayout);

//        mInternalOnAdapterChangeListener = new ViewPager2.OnAdapterChangeListener() {
//            @Override
//            public void onAdapterChanged(@NonNull ViewPager2 viewPager, @Nullable RecyclerView.Adapter oldAdapter, @Nullable RecyclerView.Adapter newAdapter) {
//                handleOnAdapterChanged(viewPager, oldAdapter, newAdapter);
//            }
//        };

        setupWithViewPager(mTabLayout, mViewPager);
    }

    //
    // public methods
    //

    /**
     * Retrieve underlying TabLayout instance.
     *
     * @return TabLayout instance
     */
    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    /**
     * Retrieve ViewPager instance.
     *
     * @return ViewPager instance
     */
    public ViewPager2 getViewPager() {
        return mViewPager;
    }

    /**
     * Sets auto tab mode adjustment enabled
     *
     * @param enabled True for enabled, otherwise false.
     */
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
     * Gets whether auto tab mode adjustment is enabled.
     *
     * @return True for enabled, otherwise false.
     */
    public boolean isAutoAdjustTabModeEnabled() {
        return mAutoAdjustTabMode;
    }

    /**
     * Unregister internal listener objects, release object references, etc.
     * This method should be called in order to avoid memory leaks.
     */
    public void release() {
        cancelPendingAdjustTabMode();
        cancelPendingSetTabsFromPagerAdapter();
        cancelPendingUpdateScrollPosition();

//        if (mInternalOnAdapterChangeListener != null) {
//            mViewPager.removeOnAdapterChangeListener(mInternalOnAdapterChangeListener);
//            mInternalOnAdapterChangeListener = null;
//        }
        if (mInternalDataSetObserver != null) {
//            mViewPager.getAdapter().unregisterDataSetObserver(mInternalDataSetObserver);
            mInternalDataSetObserver = null;
        }
        if (mInternalOnTabSelectedListener != null) {
            mTabLayout.removeOnTabSelectedListener(mInternalOnTabSelectedListener);
            mInternalOnTabSelectedListener = null;
        }
        if (mInternalTabLayoutOnPageChangeListener != null) {
//            mViewPager.removeOnPageChangeListener(mInternalTabLayoutOnPageChangeListener);
            mInternalTabLayoutOnPageChangeListener = null;
        }
        mViewPager = null;
        mTabLayout = null;
    }

    public void updateAllTabs() {
        int count = mTabLayout.getTabCount();
        for (int i = 0; i < count; i++) {
            updateTab(mTabLayout.getTabAt(i));
        }
    }

    /**
     * Override this method if you want to use custom tab layout.
     *
     * @param tabLayout TabLayout
     * @param position  Position of the item
     * @return TabLayout.Tab
     */
    protected TabLayout.Tab onCreateTab(TabLayout tabLayout, int position) {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText(mTabTitleGenerator.getTitle(position));
        return tab;
    }

    /**
     * Override this method if you want to use custom tab layout
     *
     * @param tab Tab
     */
    protected void onUpdateTab(TabLayout.Tab tab) {
        if (tab.getCustomView() == null) {
            tab.setCustomView(null); // invokes update() method internally.
        }
    }

    //
    // internal methods
    //
    protected void handleOnDataSetChanged() {
        cancelPendingUpdateScrollPosition();
        cancelPendingSetTabsFromPagerAdapter();

        if (mSetTabsFromPagerAdapterRunnable == null) {
            mSetTabsFromPagerAdapterRunnable = new Runnable() {
                @Override
                public void run() {
                    setTabsFromPagerAdapter(mTabLayout, mViewPager.getAdapter(), mViewPager.getCurrentItem());
                }
            };
        }

        mTabLayout.post(mSetTabsFromPagerAdapterRunnable);
    }

    protected void handleOnTabSelected(TabLayout.Tab tab) {
        if (mDuringSetTabsFromPagerAdapter) {
            return;
        }
        mViewPager.setCurrentItem(tab.getPosition());
        cancelPendingUpdateScrollPosition();
    }

    protected void handleOnTabUnselected(TabLayout.Tab tab) {
        if (mDuringSetTabsFromPagerAdapter) {
            return;
        }
    }

    protected void handleOnTabReselected(TabLayout.Tab tab) {
        if (mDuringSetTabsFromPagerAdapter) {
            return;
        }
    }

    protected void handleOnAdapterChanged(ViewPager2 viewPager, RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        if (mViewPager != viewPager) {
            return;
        }

        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mInternalDataSetObserver);
        }
        if (newAdapter != null) {
            newAdapter.registerAdapterDataObserver(mInternalDataSetObserver);
        }

        setTabsFromPagerAdapter(mTabLayout, newAdapter, mViewPager.getCurrentItem());
    }

    protected void cancelPendingAdjustTabMode() {
        if (mAdjustTabModeRunnable != null) {
            mTabLayout.removeCallbacks(mAdjustTabModeRunnable);
            mAdjustTabModeRunnable = null;
        }
    }

    protected void cancelPendingSetTabsFromPagerAdapter() {
        if (mSetTabsFromPagerAdapterRunnable != null) {
            mTabLayout.removeCallbacks(mSetTabsFromPagerAdapterRunnable);
            mSetTabsFromPagerAdapterRunnable = null;
        }
    }

    protected void cancelPendingUpdateScrollPosition() {
        if (mUpdateScrollPositionRunnable != null) {
            mTabLayout.removeCallbacks(mUpdateScrollPositionRunnable);
            mUpdateScrollPositionRunnable = null;
        }
    }

    protected void adjustTabMode(int prevScrollX) {
        if (mAdjustTabModeRunnable != null) {
            return;
        }

        if (prevScrollX < 0) {
            prevScrollX = mTabLayout.getScrollX();
        }

        if (ViewCompat.isLaidOut(mTabLayout)) {
            adjustTabModeInternal(mTabLayout, prevScrollX);
        } else {
            final int prevScrollX1 = prevScrollX;
            mAdjustTabModeRunnable = new Runnable() {
                @Override
                public void run() {
                    mAdjustTabModeRunnable = null;
                    adjustTabModeInternal(mTabLayout, prevScrollX1);
                }
            };
            mTabLayout.post(mAdjustTabModeRunnable);
        }
    }

    protected TabLayout.Tab createNewTab(TabLayout tabLayout, int position) {
        return onCreateTab(tabLayout, position);
    }

    protected void setupWithViewPager(@NonNull TabLayout tabLayout, @NonNull ViewPager2 viewPager) {
        final RecyclerView.Adapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }

        setTabsFromPagerAdapter(tabLayout, adapter, viewPager.getCurrentItem());

        viewPager.getAdapter().registerAdapterDataObserver(mInternalDataSetObserver);

        viewPager.registerOnPageChangeCallback(mInternalTabLayoutOnPageChangeListener);
//        viewPager.addOnAdapterChangeListener(mInternalOnAdapterChangeListener);

        tabLayout.addOnTabSelectedListener(mInternalOnTabSelectedListener);
    }

    protected void setTabsFromPagerAdapter(@NonNull TabLayout tabLayout, @Nullable RecyclerView.Adapter adapter, int currentItem) {
        try {
            mDuringSetTabsFromPagerAdapter = true;

            int prevSelectedTab = tabLayout.getSelectedTabPosition();
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
                currentItem = Math.min(currentItem, count - 1);
                if (currentItem >= 0) {
                    tabLayout.getTabAt(currentItem).select();
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

    protected void updateTab(TabLayout.Tab tab) {
        onUpdateTab(tab);
    }

    protected int determineTabMode(@NonNull TabLayout tabLayout) {
        LinearLayout slidingTabStrip = (LinearLayout) tabLayout.getChildAt(0);

        int childCount = slidingTabStrip.getChildCount();

        // NOTE: slidingTabStrip.getMeasuredWidth() method does not return correct width!
        // Need to measure each tabs and calculate the sum of them.

        int tabLayoutWidth = tabLayout.getMeasuredWidth() - tabLayout.getPaddingLeft() - tabLayout.getPaddingRight();
        int tabLayoutHeight = tabLayout.getMeasuredHeight() - tabLayout.getPaddingTop() - tabLayout.getPaddingBottom();

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

    protected void adjustTabModeInternal(@NonNull TabLayout tabLayout, int prevScrollX) {
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
                mUpdateScrollPositionRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mUpdateScrollPositionRunnable = null;
                        updateScrollPosition();
                    }
                };
                mTabLayout.post(mUpdateScrollPositionRunnable);
            }
        }
    }

    private void updateScrollPosition() {
        mTabLayout.setScrollPosition(mTabLayout.getSelectedTabPosition(), 0, false);
    }

    protected static class FixedTabLayoutOnPageChangeListener extends ViewPager2.OnPageChangeCallback {
        private final WeakReference<TabLayout> mTabLayoutRef;
        private int mPreviousScrollState;
        private int mScrollState;

        public FixedTabLayoutOnPageChangeListener(TabLayout tabLayout) {
            mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null) {
                if (shouldUpdateScrollPosition()) {
                    // Update the scroll position, only update the text selection if we're being
                    // dragged (or we're settling after a drag)
                    final boolean updateText = (mScrollState == ViewPager2.SCROLL_STATE_DRAGGING)
                            || (mScrollState == ViewPager2.SCROLL_STATE_SETTLING
                            && mPreviousScrollState == ViewPager2.SCROLL_STATE_DRAGGING);
                    tabLayout.setScrollPosition(position, positionOffset, updateText);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position) {
                // Select the tab, only updating the indicator if we're not being dragged/settled
                // (since onPageScrolled will handle that).
                Internal.selectTab(tabLayout, tabLayout.getTabAt(position),
                        mScrollState == ViewPager2.SCROLL_STATE_IDLE);
            }
        }

        private boolean shouldUpdateScrollPosition() {
            return (mScrollState == ViewPager2.SCROLL_STATE_DRAGGING) ||
                    ((mScrollState == ViewPager2.SCROLL_STATE_SETTLING) && (mPreviousScrollState == ViewPager2.SCROLL_STATE_DRAGGING));
        }
    }


    static class Internal {
        private static final Method mMethodSelectTab;

        static {
            mMethodSelectTab = getAccessiblePrivateMethod(TabLayout.class, "selectTab", TabLayout.Tab.class, boolean.class);
        }

        private static Method getAccessiblePrivateMethod(Class<?> targetClass, String methodName, Class<?>... params) throws RuntimeException {
            try {
                Method m = targetClass.getDeclaredMethod(methodName, params);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }

        public static void selectTab(TabLayout tabLayout, TabLayout.Tab tab, boolean updateIndicator) {
            try {
                mMethodSelectTab.invoke(tabLayout, tab, updateIndicator);
            } catch (IllegalAccessException e) {
                new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw handleInvocationTargetException(e);
            }
        }

        private static RuntimeException handleInvocationTargetException(InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException) targetException;
            } else {
                throw new IllegalStateException(targetException);
            }
        }
    }

    public interface TabTitleGenerator {
        String getTitle(int position);
    }
}
