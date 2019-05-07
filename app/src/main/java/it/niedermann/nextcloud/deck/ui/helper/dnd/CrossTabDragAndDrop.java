package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.graphics.Point;
import android.view.DragEvent;
import android.view.View;

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

    public CrossTabDragAndDrop(){
        this.pxToReact = 20;
        this.msToReact = 500;
    }

    public CrossTabDragAndDrop(int pixelsUntilLeftRightReaction, long millisTimeoutForSwappingTab){
        this.pxToReact = pixelsUntilLeftRightReaction;
        this.msToReact = millisTimeoutForSwappingTab;
    }

    public void register(final MainActivity mainActivity, final ViewPager viewPager, final IDragUpDown upDownDrag, final IDragLeftRight leftRightDrag){
        viewPager.setOnDragListener((View v, DragEvent dragEvent) -> {
            if(dragEvent.getAction() == 4) {
                DeckLog.log(dragEvent.getAction() + "");
            }

            View cardView = (View) dragEvent.getLocalState();
            RecyclerView recyclerView = (RecyclerView) cardView.getParent();
            CardAdapter cardAdapter = (CardAdapter) recyclerView.getAdapter();

            switch(dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_LOCATION:
                    Point size = new Point();
                    mainActivity.getWindowManager().getDefaultDisplay().getSize(size);
                    long now = System.currentTimeMillis();
                    if (lastSwap + msToReact < now){ // don't change Tabs so fast!
                        if(dragEvent.getX() <= pxToReact) {
                            DeckLog.log(dragEvent.getAction() + " moved left");
//                            cardAdapter.removeItem(viewPager.getCurrentItem());
                            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

                            FullCard itemToMove = cardAdapter.getItem(viewPager.getCurrentItem());
                            cardAdapter.removeItem(viewPager.getCurrentItem());
                            StackAdapter newAdapter = ((StackAdapter)viewPager.getAdapter());
                            StackFragment newFragment = newAdapter.getItem(viewPager.getCurrentItem());

                            recyclerView = newFragment.recyclerView;
                            cardAdapter = (CardAdapter) recyclerView.getAdapter();

                            cardAdapter.addItem(itemToMove);
                            View viewUnder = newFragment.recyclerView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());
                            if(viewUnder != null) {
                                DeckLog.log("--- " + viewUnder.getClass());
                            } else {
                                DeckLog.log("--- viewUnder is null");
                            }
//                            mainActivity.stackAdapter.get
                            lastSwap = now;
                        } else if(dragEvent.getX() >= size.x - pxToReact) {
                            DeckLog.log(dragEvent.getAction() + " moved right");
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            lastSwap = now;
                        }
                    }

                    View viewUnder = recyclerView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());
                    if(viewUnder != null) {
                        DeckLog.log("--- " + viewUnder.getClass());
                        int viewUnderPosition = recyclerView.getChildAdapterPosition(viewUnder);
                        if (viewUnderPosition != -1) {
                            DeckLog.log(dragEvent.getAction() + " moved something...");
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
}
