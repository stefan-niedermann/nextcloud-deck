package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.app.Activity;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import java.util.Date;

import it.niedermann.nextcloud.deck.DeckConsts;
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

    public void register(final Activity source, final ViewPager subject, IDragUpDown upDownDrag, IDragLeftRight leftRightDrag){
        subject.setOnDragListener((View v, DragEvent dragEvent) -> {
            Log.d(DeckConsts.DEBUG_TAG, "Drag: "+ dragEvent.getAction());
            if(dragEvent.getAction() == 4)
                Log.d(DeckConsts.DEBUG_TAG, dragEvent.getAction() + "");

            View view = (View) dragEvent.getLocalState();
            RecyclerView owner = (RecyclerView) view.getParent();
            CardAdapter cardAdapter = (CardAdapter) owner.getAdapter();

            switch(dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_LOCATION:
                    Point size = new Point();
                    source.getWindowManager().getDefaultDisplay().getSize(size);
                    long now = new Date().getTime();
                    if (lastSwap+msToReact < now){ // don't change Tabs so fast!
                        if(dragEvent.getX() <= pxToReact) {
                            Log.d(DeckConsts.DEBUG_TAG, dragEvent.getAction() + " moved left");
                            subject.setCurrentItem(subject.getCurrentItem() - 1);
                            lastSwap = now;
                        } else if(dragEvent.getX() >= size.x - pxToReact) {
                            Log.d(DeckConsts.DEBUG_TAG, dragEvent.getAction() + " moved right");
                            subject.setCurrentItem(subject.getCurrentItem() + 1);
                            lastSwap = now;
                        }
                    } else {
                        Log.d(DeckConsts.DEBUG_TAG, "drag blocked");
                    }
                    int viewUnderPosition = owner.getChildAdapterPosition(owner.findChildViewUnder(dragEvent.getX(), dragEvent.getY()));
                    if(viewUnderPosition != -1) {
                        Log.d(DeckConsts.DEBUG_TAG, dragEvent.getAction() + " moved something...");
                        cardAdapter.moveItem(owner.getChildLayoutPosition(view), viewUnderPosition);
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
