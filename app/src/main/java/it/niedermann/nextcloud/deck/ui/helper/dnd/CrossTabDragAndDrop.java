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
        viewPager.setCurrentItem(newPosition);
        draggedCardLocalState.onTabChanged(viewPager, newPosition);

        final RecyclerView recyclerView = draggedCardLocalState.getRecyclerView();
        CardAdapter cardAdapter = draggedCardLocalState.getCardAdapter();

        //insert card in new tab
        View firstVisibleView = recyclerView.getChildAt(0);
        int positionToInsert = firstVisibleView == null ? 0 : recyclerView.getChildAdapterPosition(firstVisibleView)+1;

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

        if (oldCardPosition != -1){
            cardAdapter.removeItem(oldCardPosition);
            DeckLog.log("DnD: removed");
        } else DeckLog.log("DnD: wasnt present to remove");
    }
}
