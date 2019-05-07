package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.content.ClipData;
import android.graphics.Point;
import android.view.DragEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;

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

    public void register(final MainActivity mainActivity, final ViewPager viewPager, final IDragUpDown upDownDrag, final IDragLeftRight leftRightDrag) {
        viewPager.setOnDragListener((View v, DragEvent dragEvent) -> {
            if (dragEvent.getAction() == 4) {
                DeckLog.log("--- " + dragEvent.getAction() + "");
            }

            final CardView cardView = (CardView) dragEvent.getLocalState();

            if (cardView == null) {
                return true;
            }

            final RecyclerView recyclerView = (RecyclerView) cardView.getParent();

            if (recyclerView == null) {
                return true;
            }
            CardAdapter cardAdapter = (CardAdapter) recyclerView.getAdapter();

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_LOCATION:
                    Point size = new Point();
                    mainActivity.getWindowManager().getDefaultDisplay().getSize(size);
                    long now = System.currentTimeMillis();
                    if (lastSwap + msToReact < now) { // don't change Tabs so fast!
                        if (dragEvent.getX() <= pxToReact) {
                            int oldCardPosition = recyclerView.getChildAdapterPosition(cardView);
                            int oldTabPosition = viewPager.getCurrentItem();
                            log("Old Card index: " + oldCardPosition + " | Old Tab: " + oldTabPosition);

                            FullCard itemToMove = cardAdapter.getItem(oldCardPosition);
                            log("Card: " + itemToMove.card.getTitle());

                            log("Remove card from position " + oldCardPosition);
                            cardAdapter.removeItem(oldCardPosition);

                            log("Select Tab " + (oldTabPosition - 1));
                            viewPager.setCurrentItem(oldTabPosition - 1);

                            StackAdapter newAdapter = ((StackAdapter) viewPager.getAdapter());
                            StackFragment newFragment = newAdapter.getItem(viewPager.getCurrentItem());
                            log("new stackfragment id: " + newFragment.getStackId());
                            final RecyclerView newrecyclerView = newFragment.recyclerView;
                            cardAdapter = (CardAdapter) newrecyclerView.getAdapter();

                            int insertedPosition = cardAdapter.addItem(itemToMove);
                            log("Inserted on position " + insertedPosition);

                            newrecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                                @Override
                                public void onChildViewAttachedToWindow(@NonNull View view) {
                                    newrecyclerView.removeOnChildAttachStateChangeListener(this);
                                    CardView cardView = (CardView) view;

                                    log(cardView + "");
//                                    cardView.setVisibility(View.INVISIBLE);

                                    ClipData dragData = ClipData.newPlainText("TEST", "TEST2");
                                    cardView.startDrag(dragData,  // the data to be dragged
                                            new View.DragShadowBuilder(){
                                                @Override
                                                public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
                                                    outShadowSize.set(1,1);
                                                    outShadowTouchPoint.set(0,0);
                                                }
                                            },  // the drag shadow builder
                                            cardView,      // no need to use local data
                                            0          // flags (not currently used, set to 0)
                                    );
//                                    cardView.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onChildViewDetachedFromWindow(@NonNull View view) {

                                }
                            });

                            cardAdapter.notifyDataSetChanged();

                            lastSwap = now;



//                                ClipData dragData = ClipData.newPlainText("TEST", "TEST2");
//                                cardView.startDrag(dragData,  // the data to be dragged
//                                        new View.DragShadowBuilder(cardView),  // the drag shadow builder
//                                        cardView,      // no need to use local data
//                                        0          // flags (not currently used, set to 0)
//                                );
//                                viewPager.startDragAndDrop()

//                            View viewUnder = newFragment.recyclerView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());
                        } else if (dragEvent.getX() >= size.x - pxToReact) {
                            DeckLog.log(dragEvent.getAction() + " moved right");
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            lastSwap = now;
                        }
                    }

                    //push around the other cards
                    View viewUnder = recyclerView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());
                    if (viewUnder != null) {
                        DeckLog.log("--- " + viewUnder.getClass());
                        int viewUnderPosition = recyclerView.getChildAdapterPosition(viewUnder);
                        if (viewUnderPosition != -1) {
                            Objects.requireNonNull(cardAdapter).moveItem(recyclerView.getChildLayoutPosition(cardView), viewUnderPosition);
                        }
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    cardView.setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        });
    }

    private void log(String s) {
        DeckLog.log("--- Drag 'n' Drop: " + s);
    }
}
