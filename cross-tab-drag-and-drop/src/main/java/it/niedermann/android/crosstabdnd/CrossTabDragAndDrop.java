package it.niedermann.android.crosstabdnd;

import android.content.res.Resources;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CrossTabDragAndDrop<
        TabFragment extends Fragment & DragAndDropTab<ItemAdapter>,
        ItemAdapter extends RecyclerView.Adapter<?> & DragAndDropAdapter<ItemModel>,
        ItemModel extends DragAndDropModel
        > {

    private static final String TAG = CrossTabDragAndDrop.class.getCanonicalName();
    private static final ScrollHelper SCROLL_HELPER = new ScrollHelper();

    private final float pxToReact;
    private final float pxToReactTopBottom;
    private final int dragAndDropMsToReact;
    private final int dragAndDropMsToReactTopBottom;
    private final int displayX;
    private long lastSwap = 0;
    private long lastMove = 0;

    private final Set<ItemMovedByDragListener<ItemModel>> moveListenerList = new HashSet<>(1);

    public CrossTabDragAndDrop(@NonNull Resources resources) {
        this.displayX = resources.getDisplayMetrics().widthPixels;
        final float density = resources.getDisplayMetrics().density;
        this.pxToReact = resources.getInteger(R.integer.drag_n_drop_dp_to_react) * density;
        this.dragAndDropMsToReactTopBottom = resources.getInteger(R.integer.drag_n_drop_dp_to_react_top_bottom);
        this.pxToReactTopBottom = dragAndDropMsToReactTopBottom * density;
        this.dragAndDropMsToReact = resources.getInteger(R.integer.drag_n_drop_ms_to_react);
    }

    public void register(final ViewPager2 viewPager, TabLayout stackLayout, FragmentManager fm) {
        viewPager.setOnDragListener((View v, DragEvent dragEvent) -> {
            DraggedItemLocalState<TabFragment, ItemAdapter, ItemModel> draggedItemLocalState = (DraggedItemLocalState<TabFragment, ItemAdapter, ItemModel>) dragEvent.getLocalState();
            View draggedView = draggedItemLocalState.getDraggedView();
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    draggedView.setVisibility(View.INVISIBLE);
                    draggedItemLocalState.onDragStart(viewPager, fm);
                    break;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    RecyclerView currentRecyclerView = draggedItemLocalState.getRecyclerView();
                    ItemAdapter itemAdapter = draggedItemLocalState.getItemAdapter();

                    long now = System.currentTimeMillis();
                    if (lastSwap + dragAndDropMsToReact < now) { // don't change Tabs so fast!
                        int oldTabPosition = viewPager.getCurrentItem();

                        boolean shouldSwitchTab = true;

                        int newTabPosition = -1;
                        // change tab? if yes, which direction?
                        if (dragEvent.getX() <= pxToReact) {
                            newTabPosition = oldTabPosition - 1;
                        } else if (dragEvent.getX() >= displayX - pxToReact) {
                            newTabPosition = oldTabPosition + 1;
                        } else {
                            shouldSwitchTab = false;
                        }

                        if (shouldSwitchTab && isMovePossible(viewPager, newTabPosition)) {
                            removeItem(currentRecyclerView, draggedView, itemAdapter);
                            detectAndKillDuplicatesInNeighbourTab(viewPager, draggedItemLocalState.getDraggedItemModel(), fm, oldTabPosition, newTabPosition);
                            switchTab(dragEvent, viewPager, stackLayout, fm, draggedItemLocalState, now, newTabPosition);

                            return true;
                        }
                    }

                    //scroll if needed
                    if (dragEvent.getY() <= pxToReactTopBottom) {
                        SCROLL_HELPER.startScroll(currentRecyclerView, ScrollHelper.ScrollDirection.UP);
                    } else if (dragEvent.getY() >= currentRecyclerView.getBottom() - pxToReactTopBottom) {
                        SCROLL_HELPER.startScroll(currentRecyclerView, ScrollHelper.ScrollDirection.DOWN);
                    } else {
                        SCROLL_HELPER.stopScroll();
                    }

                    if (lastMove + dragAndDropMsToReactTopBottom < now) {
                        //push around the other items
                        pushAroundItems(draggedView, currentRecyclerView, dragEvent, itemAdapter, draggedItemLocalState, now);
                    }
                    break;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    draggedItemLocalState.getRecyclerView().removeOnChildAttachStateChangeListener(draggedItemLocalState.getInsertedListener());
                    SCROLL_HELPER.stopScroll();
                    draggedView.setVisibility(View.VISIBLE);
                    // Clean up the original dragged view, so the next onBindViewHolder() will not display the view at the position of the original dragged view as View.INVISIBLE
                    draggedItemLocalState.getOriginalDraggedView().setVisibility(View.VISIBLE);
                    notifyListeners(draggedItemLocalState);
                    break;
                }
            }
            return true;
        });
    }

    private void switchTab(DragEvent dragEvent, ViewPager2 viewPager, TabLayout stackLayout, FragmentManager fm, final DraggedItemLocalState<TabFragment, ItemAdapter, ItemModel> draggedItemLocalState, long now, int newPosition) {
        viewPager.setCurrentItem(newPosition);
        draggedItemLocalState.onTabChanged(viewPager, fm);
        Objects.requireNonNull(stackLayout.getTabAt(newPosition)).select();

        final RecyclerView recyclerView = draggedItemLocalState.getRecyclerView();
        final ItemAdapter itemAdapter = draggedItemLocalState.getItemAdapter();

        RecyclerView.OnChildAttachStateChangeListener onChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                recyclerView.removeOnChildAttachStateChangeListener(this);
                draggedItemLocalState.setInsertedListener(null);
                view.setVisibility(View.INVISIBLE);
                draggedItemLocalState.setDraggedView(view);
                pushAroundItems(view, recyclerView, dragEvent, itemAdapter, (DraggedItemLocalState<TabFragment, ItemAdapter, ItemModel>) draggedItemLocalState, now);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {/* do nothing */}
        };
        draggedItemLocalState.setInsertedListener(onChildAttachStateChangeListener);
        recyclerView.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener);

        //insert item in new tab
        View firstVisibleView = recyclerView.getChildAt(0);
        int positionToInsert = firstVisibleView == null ? 0 : recyclerView.getChildAdapterPosition(firstVisibleView) + 1;
        itemAdapter.insertItem(draggedItemLocalState.getDraggedItemModel(), positionToInsert);

        lastSwap = now;
    }

    private void pushAroundItems(@NonNull View view, RecyclerView recyclerView, DragEvent dragEvent, ItemAdapter itemAdapter, DraggedItemLocalState<TabFragment, ItemAdapter, ItemModel> draggedItemLocalState, long now) {
        View viewUnder = recyclerView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());

        if (viewUnder != null) {
            int toPositon = recyclerView.getChildAdapterPosition(viewUnder);
            if (toPositon != -1) {
                int fromPosition = recyclerView.getChildAdapterPosition(view);
                if (fromPosition != -1 && fromPosition != toPositon) {
                    recyclerView.post(() -> {
                        itemAdapter.moveItem(fromPosition, toPositon);
                        draggedItemLocalState.setPositionInItemAdapter(toPositon);
                    });
                    lastMove = now;
                }
            }
        }
    }

    private static boolean isMovePossible(ViewPager2 viewPager, int newPosition) {
        return newPosition < Objects.requireNonNull(viewPager.getAdapter()).getItemCount() && newPosition >= 0;
    }

    private void detectAndKillDuplicatesInNeighbourTab(ViewPager2 viewPager, ItemModel itemToFind, FragmentManager fm, int oldTabPosition, int newTabPosition) {
        int tabPositionToCheck = newTabPosition > oldTabPosition ? newTabPosition + 1 : newTabPosition - 1;

        if (isMovePossible(viewPager, tabPositionToCheck)) {
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    viewPager.unregisterOnPageChangeCallback(this);
                    ItemAdapter itemAdapter = DragAndDropUtil.<TabFragment>getTabFragment(fm, Objects.requireNonNull(viewPager.getAdapter()).getItemId(tabPositionToCheck)).getAdapter();
                    itemAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();
                            itemAdapter.unregisterAdapterDataObserver(this);
                            List<ItemModel> itemList = itemAdapter.getItemList();
                            for (int i = 0; i < itemList.size(); i++) {
                                ItemModel c = itemList.get(i);
                                if (itemToFind.getComparableId().equals(c.getComparableId())) {
                                    itemAdapter.removeItem(i);
                                    itemAdapter.notifyItemRemoved(i);
                                    Log.v(TAG, "DnD removed dupe at tab " + tabPositionToCheck + ": " + c.toString());
                                    break;
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    private void removeItem(RecyclerView currentRecyclerView, View view, ItemAdapter itemAdapter) {
        int oldItemPosition = currentRecyclerView.getChildAdapterPosition(view);

        if (oldItemPosition != -1) {
            itemAdapter.removeItem(oldItemPosition);
        }
    }

    private void notifyListeners(DraggedItemLocalState draggedItemLocalState) {
        for (ItemMovedByDragListener listener : moveListenerList) {
            listener.onItemMoved(draggedItemLocalState.getDraggedItemModel(), draggedItemLocalState.getCurrentTabId(), draggedItemLocalState.getPositionInItemAdapter());
        }
    }

    public void addItemMovedByDragListener(ItemMovedByDragListener<ItemModel> listener) {
        moveListenerList.add(listener);
    }
}
