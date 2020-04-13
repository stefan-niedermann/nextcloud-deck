package it.niedermann.android.tablayouthelper;

import android.util.Log;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class FixedTabLayoutOnPageChangeListener extends ViewPager2.OnPageChangeCallback {
    private final WeakReference<TabLayout> mTabLayoutRef;
    private int mPreviousScrollState;
    private int mScrollState;

    FixedTabLayoutOnPageChangeListener(TabLayout tabLayout) {
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
        if (tabLayout != null && shouldUpdateScrollPosition()) {
            // Update the scroll position, only update the text selection if we're being
            // dragged (or we're settling after a drag)
            tabLayout.setScrollPosition(position, positionOffset, true);
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

    private static class Internal {
        private static final Method mMethodSelectTab;

        static {
            mMethodSelectTab = getAccessiblePrivateMethod(TabLayout.class, "selectTab", TabLayout.Tab.class, boolean.class);
        }

        @SuppressWarnings("SameParameterValue")
        private static Method getAccessiblePrivateMethod(Class<?> targetClass, String methodName, Class<?>... params) throws RuntimeException {
            try {
                Method m = targetClass.getDeclaredMethod(methodName, params);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }

        private static void selectTab(TabLayout tabLayout, TabLayout.Tab tab, boolean updateIndicator) {
            try {
                mMethodSelectTab.invoke(tabLayout, tab, updateIndicator);
            } catch (IllegalAccessException e) {
                Log.e(TabLayoutHelper.class.getCanonicalName(), e.getMessage(), new IllegalStateException(e));
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
}