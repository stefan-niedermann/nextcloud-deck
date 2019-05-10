package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.app.Activity;
import android.graphics.Point;
import android.os.Handler;
import android.view.DragEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;

public class CrossTabDragAndDrop {

    private static final int SROLL_SPEED = 200;

    private enum ScrollDirection {
        UP,
        DOWN
    }

    private static class ScrollHelper implements Runnable {
        private boolean shouldScroll = false;
        private ScrollDirection scrollDirection;
        private RecyclerView currentRecyclerView;
        Handler handler = new Handler();

        public void startScroll(RecyclerView recyclerView, ScrollDirection scrollDirection) {
            this.scrollDirection = scrollDirection;
            this.currentRecyclerView = recyclerView;
            if (!shouldScroll) {
                this.shouldScroll = true;
                handler.post(this);
            }
        }

        public void stopScroll() {
            this.shouldScroll = false;
        }

        @Override
        public void run() {
            if (scrollDirection == ScrollDirection.UP) {
                currentRecyclerView.smoothScrollBy(0, SROLL_SPEED * -1);
            } else {
                currentRecyclerView.smoothScrollBy(0, SROLL_SPEED);
            }
            if (shouldScroll) {
                handler.postDelayed(this, 100);
            }
        }
    }

    private static final ScrollHelper SCROLL_HELPER = new ScrollHelper();

    private final Activity activity;
    private final float pxToReact;
    private final long msToReact;
    private long lastSwap = 0;

    private final float pxToReactTopBottom;

    public CrossTabDragAndDrop(Activity activity) {
        this.activity = activity;
        final float density = activity.getResources().getDisplayMetrics().density;
        this.pxToReact = activity.getResources().getInteger(R.integer.drag_n_drop_dp_to_react) * density;
        this.pxToReactTopBottom = activity.getResources().getInteger(R.integer.drag_n_drop_dp_to_react_top_bottom) * density;
        this.msToReact = activity.getResources().getInteger(R.integer.drag_n_drop_ms_to_react);
    }

    public void register(final ViewPager viewPager) {
        viewPager.setOnDragListener((View v, DragEvent dragEvent) -> {

            DraggedCardLocalState draggedCardLocalState = (DraggedCardLocalState) dragEvent.getLocalState();
            CardView cardView = draggedCardLocalState.getDraggedView();
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    cardView.setVisibility(View.INVISIBLE);
                    draggedCardLocalState.setRecyclerView(((StackAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem()).getRecyclerView());
                    break;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    RecyclerView currentRecyclerView = draggedCardLocalState.getRecyclerView();
                    CardAdapter cardAdapter = draggedCardLocalState.getCardAdapter();

                    Point size = new Point();
                    activity.getWindowManager().getDefaultDisplay().getSize(size);

                    long now = System.currentTimeMillis();
                    if (lastSwap + msToReact < now) { // don't change Tabs so fast!
                        int oldTabPosition = viewPager.getCurrentItem();

                        boolean shouldSwitchTab = true;

                        int newTabPosition = -1;
                        // change tab? if yes, which direction?
                        if (dragEvent.getX() <= pxToReact) {
                            newTabPosition = oldTabPosition - 1;
                        } else if (dragEvent.getX() >= size.x - pxToReact) {
                            newTabPosition = oldTabPosition + 1;
                        } else {
                            shouldSwitchTab = false;
                        }

                        if (shouldSwitchTab && isMovePossible(viewPager, newTabPosition)) {
                            removeItem(currentRecyclerView, cardView, cardAdapter);
                            moveCardToTab(viewPager, draggedCardLocalState, now, newTabPosition);
                            return true;
                        }
                    }

                    //scroll if needed
                    if (dragEvent.getY() <= pxToReactTopBottom) {
                        SCROLL_HELPER.startScroll(currentRecyclerView, ScrollDirection.UP);
                    } else if (dragEvent.getY() >= currentRecyclerView.getBottom() - pxToReactTopBottom) {
                        SCROLL_HELPER.startScroll(currentRecyclerView, ScrollDirection.DOWN);
                    } else {
                        SCROLL_HELPER.stopScroll();
                    }

                    //push around the other cards
                    View viewUnder = currentRecyclerView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());

                    if (viewUnder != null) {
                        int toPositon = currentRecyclerView.getChildAdapterPosition(viewUnder);
                        if (toPositon != -1) {
                            int fromPosition = currentRecyclerView.getChildAdapterPosition(cardView);
                            if (fromPosition != -1) {
                                cardAdapter.moveItem(fromPosition, toPositon);
                                draggedCardLocalState.setPositionInCardAdapter(toPositon);
                            }
                        }
                    }
                    break;
                }
                case DragEvent.ACTION_DROP: {
                    cardView.setVisibility(View.VISIBLE);
                    break;
                }
            }
            return true;
        });
    }

    private void moveCardToTab(ViewPager viewPager, final DraggedCardLocalState draggedCardLocalState, long now, int newPosition) {
        draggedCardLocalState.onTabChanged(viewPager, newPosition);
        viewPager.setCurrentItem(newPosition);

        final RecyclerView recyclerView = draggedCardLocalState.getRecyclerView();
        CardAdapter cardAdapter = draggedCardLocalState.getCardAdapter();

        //insert card in new tab
        View firstVisibleView = recyclerView.getChildAt(0);
        int positionToInsert = firstVisibleView == null ? 0 : recyclerView.getChildAdapterPosition(firstVisibleView) + 1;

        cardAdapter.addItem(draggedCardLocalState.getDraggedCard(), positionToInsert);

        RecyclerView.OnChildAttachStateChangeListener onChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                recyclerView.removeOnChildAttachStateChangeListener(this);
                draggedCardLocalState.setInsertedListener(null);
                CardView cardView = (CardView) view;
                cardView.setVisibility(View.INVISIBLE);
                draggedCardLocalState.setDraggedView(cardView);
                DeckLog.log("dnd there it is! pos: " + positionToInsert);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {/* do nothing */}
        };
        draggedCardLocalState.setInsertedListener(onChildAttachStateChangeListener);
        recyclerView.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener);

        cardAdapter.notifyItemInserted(positionToInsert);
        cardAdapter.notifyItemChanged(positionToInsert);

        lastSwap = now;
    }

    private static boolean isMovePossible(ViewPager viewPager, int newPosition) {
        return newPosition < viewPager.getAdapter().getCount() && newPosition >= 0;
    }

    private static void removeItem(RecyclerView currentRecyclerView, CardView cardView, CardAdapter cardAdapter) {
        int oldCardPosition = currentRecyclerView.getChildAdapterPosition(cardView);
        DeckLog.log("DnD: removing! pos: " + oldCardPosition + " | " + cardView);

        if (oldCardPosition != -1) {
            cardAdapter.removeItem(oldCardPosition);
            DeckLog.log("DnD: removed");
        } else DeckLog.log("DnD: wasnt present to remove");
    }
}
