package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.graphics.Point;
import android.view.DragEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;

public class CrossTabDragAndDrop {

    private final int pxToReact;
    private final long msToReact;
    private long lastSwap = 0;

    public CrossTabDragAndDrop() {
        this.pxToReact = 20;
        this.msToReact = 500;
    }

    public CrossTabDragAndDrop(int pixelsUntilLeftRightReaction, long millisTimeoutForSwappingTab) {
        this.pxToReact = pixelsUntilLeftRightReaction;
        this.msToReact = millisTimeoutForSwappingTab;
    }

    public void register(final MainActivity mainActivity, final ViewPager viewPager) {
        viewPager.setOnDragListener((View v, DragEvent dragEvent) -> {

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    CardView cardView = (CardView) dragEvent.getLocalState();
                    cardView.setVisibility(View.INVISIBLE);
                    break;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    RecyclerView currentRecyclerView = getCurrentRecyclerView(viewPager);
                    CardView cardView = findInvisibleCardView(currentRecyclerView);
                    CardAdapter cardAdapter = (CardAdapter) currentRecyclerView.getAdapter();

                    Point size = new Point();
                    mainActivity.getWindowManager().getDefaultDisplay().getSize(size);

                    FullCard itemToMove = null;
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

                        if (shouldSwitchTab && isMovePossible(viewPager, newTabPosition) && cardView != null) {
                            itemToMove = removeItemAndReturnPayload(currentRecyclerView, cardView, cardAdapter);
                            moveCardToTab(viewPager, itemToMove, now, newTabPosition);
                            return true;
                        }
                    }

                    //push around the other cards
                    View viewUnder = currentRecyclerView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());

                    CardView newCardViewToSearch = findInvisibleCardView(currentRecyclerView);

                    if (newCardViewToSearch == null) {
                        return true;
                    }

                    if (viewUnder != null) {
                        int viewUnderPosition = currentRecyclerView.getChildAdapterPosition(viewUnder);
                        if (viewUnderPosition != -1) {
                            Objects.requireNonNull(cardAdapter).moveItem(currentRecyclerView.getChildLayoutPosition(newCardViewToSearch), viewUnderPosition);
                        }
                    }
                    break;
                }
                case DragEvent.ACTION_DROP: {
                    RecyclerView currentRecyclerView = getCurrentRecyclerView(viewPager);
                    CardView cardView = findInvisibleCardView(currentRecyclerView);
                    cardView.setVisibility(View.VISIBLE);
                    break;
                }
            }
            return true;
        });
    }

    private void moveCardToTab(ViewPager viewPager, FullCard itemToMove, long now, int newPosition) {
        viewPager.setCurrentItem(newPosition);

        final RecyclerView recyclerView = getCurrentRecyclerView(viewPager);
        CardAdapter cardAdapter = (CardAdapter) recyclerView.getAdapter();

        //insert card in new tab
        View viewUnder = recyclerView.getChildAt(0);
        int insertedPosition = viewUnder == null ? 0 : recyclerView.getChildAdapterPosition(viewUnder);

        cardAdapter.addItem(itemToMove, insertedPosition);

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                recyclerView.removeOnChildAttachStateChangeListener(this);
                CardView cardView = (CardView) view;
                cardView.setVisibility(View.INVISIBLE);
                DeckLog.log("dnd there it is");
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {/* do nothing */}
        });

        cardAdapter.notifyItemInserted(insertedPosition);

        lastSwap = now;
    }

    private static boolean isMovePossible(ViewPager viewPager, int newPosition) {
        return newPosition < viewPager.getAdapter().getCount() && newPosition >= 0;
    }

    private static CardView findInvisibleCardView(RecyclerView recyclerView) {
//        DeckLog.log("dnd number entries: "+recyclerView.getChildCount());
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View view = recyclerView.getChildAt(i);

            if (view.findViewById(R.id.card).getVisibility() == View.INVISIBLE) {
                return (CardView) view;
            }
        }
        return null;
    }

    private static RecyclerView getCurrentRecyclerView(ViewPager viewPager) {
        return ((StackAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem()).getRecyclerView();
    }

    private static FullCard removeItemAndReturnPayload(RecyclerView currentRecyclerView, CardView cardView, CardAdapter cardAdapter) {

        int oldCardPosition = currentRecyclerView.getChildAdapterPosition(cardView);
        DeckLog.log("DnD: removing! pos: " + oldCardPosition + " | " + cardView);
        FullCard itemToMove = cardAdapter.getItem(oldCardPosition);

        cardAdapter.removeItem(oldCardPosition);
        DeckLog.log("DnD: removed " + itemToMove);

        return itemToMove;
    }
}
