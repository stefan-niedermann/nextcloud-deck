package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.app.Activity;
import android.graphics.Point;
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

    private final Activity activity;
    private final float pxToReact;
    private final long msToReact;
    private long lastSwap = 0;

    public CrossTabDragAndDrop(Activity activity) {
        this.activity = activity;
        this.pxToReact = activity.getResources().getInteger(R.integer.drag_n_drop_dp_to_react) * activity.getResources().getDisplayMetrics().density;
        this.msToReact = activity.getResources().getInteger(R.integer.drag_n_drop_ms_to_react);
    }

    public void register(final ViewPager viewPager) {
        viewPager.setOnDragListener((View v, DragEvent dragEvent) -> {

            DraggedCardData draggedCardData = (DraggedCardData) dragEvent.getLocalState();
            CardView cardView = draggedCardData.getDraggedView();
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    cardView.setVisibility(View.INVISIBLE);
                    break;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    RecyclerView currentRecyclerView = getCurrentRecyclerView(viewPager);
                    CardAdapter cardAdapter = draggedCardData.getCardAdapter();

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
                            moveCardToTab(viewPager, draggedCardData, now, newTabPosition);
                            return true;
                        }
                    }

                    //push around the other cards

//                    DeckLog.log("dnd card count:" + ((CardAdapter) currentRecyclerView.getAdapter()).getCardList().size());

//                    if (cardView == null) {
////                        DeckLog.log("dnd skipping");
//                        return true;
//                    }
                    View viewUnder = currentRecyclerView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());

                    if (viewUnder != null) {
                        int viewUnderPosition = currentRecyclerView.getChildAdapterPosition(viewUnder);
                        if (viewUnderPosition != -1) {
                            cardAdapter.moveItem(currentRecyclerView.getChildLayoutPosition(cardView), viewUnderPosition);
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

    private void moveCardToTab(ViewPager viewPager, final DraggedCardData draggedCardData, long now, int newPosition) {
        viewPager.setCurrentItem(newPosition);

        final RecyclerView recyclerView = getCurrentRecyclerView(viewPager);
        CardAdapter cardAdapter = (CardAdapter) recyclerView.getAdapter();

        //insert card in new tab
        View firstVisibleView = recyclerView.getChildAt(0);
        int positionToInsert = firstVisibleView == null ? 0 : recyclerView.getChildAdapterPosition(firstVisibleView);

        cardAdapter.addItem(draggedCardData.getDraggedCard(), positionToInsert);

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                recyclerView.removeOnChildAttachStateChangeListener(this);
                CardView cardView = (CardView) view;
                cardView.setVisibility(View.INVISIBLE);
                draggedCardData.setDraggedView(cardView);
                draggedCardData.setCardAdapter((CardAdapter) recyclerView.getAdapter());
                DeckLog.log("dnd there it is! pos: " + positionToInsert);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {/* do nothing */}
        });

        cardAdapter.notifyItemInserted(positionToInsert);
        cardAdapter.notifyItemChanged(positionToInsert);

        lastSwap = now;
    }

    private static boolean isMovePossible(ViewPager viewPager, int newPosition) {
        return newPosition < viewPager.getAdapter().getCount() && newPosition >= 0;
    }

    private static RecyclerView getCurrentRecyclerView(ViewPager viewPager) {
        return ((StackAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem()).getRecyclerView();
    }

    private static void removeItem(RecyclerView currentRecyclerView, CardView cardView, CardAdapter cardAdapter) {

        int oldCardPosition = currentRecyclerView.getChildAdapterPosition(cardView);
        DeckLog.log("DnD: removing! pos: " + oldCardPosition + " | " + cardView);

        cardAdapter.removeItem(oldCardPosition);
        DeckLog.log("DnD: removed");
    }
}
