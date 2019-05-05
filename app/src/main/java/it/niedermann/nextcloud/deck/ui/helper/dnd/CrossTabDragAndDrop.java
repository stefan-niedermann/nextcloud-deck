package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.app.Activity;
import android.graphics.Point;
import android.view.DragEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;

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

    public void register(final Activity source, final ViewPager pager, final IDragUpDown upDownDrag, final IDragLeftRight leftRightDrag){
        pager.setOnDragListener((View v, DragEvent dragEvent) -> {
            if(dragEvent.getAction() == 4) {
                DeckLog.log(dragEvent.getAction() + "");
            }

            View view = (View) dragEvent.getLocalState();
            RecyclerView owner = (RecyclerView) view.getParent();
            CardAdapter cardAdapter = (CardAdapter) owner.getAdapter();

            switch(dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_LOCATION:
                    Point size = new Point();
                    source.getWindowManager().getDefaultDisplay().getSize(size);
                    long now = System.currentTimeMillis();
                    if (lastSwap + msToReact < now){ // don't change Tabs so fast!
                        if(dragEvent.getX() <= pxToReact) {
                            DeckLog.log(dragEvent.getAction() + " moved left");
                            pager.setCurrentItem(pager.getCurrentItem() - 1);
                            lastSwap = now;
                        } else if(dragEvent.getX() >= size.x - pxToReact) {
                            DeckLog.log(dragEvent.getAction() + " moved right");
                            pager.setCurrentItem(pager.getCurrentItem() + 1);
                            lastSwap = now;
                        }
                    }

                    int viewUnderPosition = owner.getChildAdapterPosition(Objects.requireNonNull(owner.findChildViewUnder(dragEvent.getX(), dragEvent.getY())));
                    if(viewUnderPosition != -1) {
                        DeckLog.log(dragEvent.getAction() + " moved something...");
                        Objects.requireNonNull(cardAdapter).moveItem(owner.getChildLayoutPosition(view), viewUnderPosition);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    view.setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        });
    }
}
