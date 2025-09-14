package it.niedermann.nextcloud.deck.deprecated.ui;

import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.deprecated.util.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.deprecated.ui.stack.StackAdapter;

public class StackChangeCallback extends ViewPager2.OnPageChangeCallback {

    private final StackAdapter adapter;
    private final ViewPager2 viewPager;
    private final ExtendedFloatingActionButton fab;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private final Menu menu;
    private final Consumer<Stack> onStackSelected;

    public StackChangeCallback(
            @NonNull StackAdapter adapter,
            @NonNull ViewPager2 viewPager,
            @NonNull ExtendedFloatingActionButton fab,
            @NonNull SwipeRefreshLayout swipeRefreshLayout,
            @NonNull Menu menu,
            @NonNull Consumer<Stack> onStackSelected
    ) {
        this.adapter = adapter;
        this.viewPager = viewPager;
        this.fab = fab;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.menu = menu;
        this.onStackSelected = onStackSelected;
    }

    @Override
    public void onPageSelected(int position) {
        this.updateMoveItemVisibility();
        this.viewPager.post(() -> {
            // stackAdapter size might differ from position when an account has been deleted
            if (this.adapter.getItemCount() > position) {
                this.onStackSelected.accept(this.adapter.getItem(position));
            } else {
                DeckLog.logError(new IllegalStateException("Tried to save current Stack which cannot be available (stackAdapter doesn't have this position)"));
            }
        });
        this.fab.extend();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setEnabled(state == ViewPager2.SCROLL_STATE_IDLE);
        }
    }

    public void updateMoveItemVisibility() {
        final var currentBoardHasStacks = adapter.getItemCount() > 0;
        final int currentViewPagerItem = viewPager.getCurrentItem();

        menu.findItem(R.id.move_list_left).setVisible(currentBoardHasStacks && currentViewPagerItem > 0);
        menu.findItem(R.id.move_list_right).setVisible(currentBoardHasStacks && currentViewPagerItem < adapter.getItemCount() - 1);
    }
}
