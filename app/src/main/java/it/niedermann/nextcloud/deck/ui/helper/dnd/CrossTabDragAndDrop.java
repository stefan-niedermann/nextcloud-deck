package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.v4.view.ViewPager;
import android.view.DragEvent;
import android.view.View;

import it.niedermann.nextcloud.deck.ui.card.CardAdapter;

public class CrossTabDragAndDrop {

    private final int pxToReact;

    public CrossTabDragAndDrop(){
        this.pxToReact = 20;
    }

    public CrossTabDragAndDrop(int pixelsUntilLeftRightReaction){
        this.pxToReact = pixelsUntilLeftRightReaction;
    }

    public void register(final Activity source, final ViewPager subject, IDragUpDown upDownDrag, IDragLeftRight leftRightDrag){
        subject.setOnDragListener((View v, DragEvent dragEvent) -> {
            if(dragEvent.getAction() == 4)
                Log.v("Deck", dragEvent.getAction() + "");

            View view = (View) dragEvent.getLocalState();
            RecyclerView owner = (RecyclerView) view.getParent();
            CardAdapter cardAdapter = (CardAdapter) owner.getAdapter();

            switch(dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_LOCATION:
                    Point size = new Point();
                    source.getWindowManager().getDefaultDisplay().getSize(size);
                    if(dragEvent.getX() <= pxToReact) {
                        subject.setCurrentItem(subject.getCurrentItem() - 1);
                    } else if(dragEvent.getX() >= size.x - pxToReact) {
                        subject.setCurrentItem(subject.getCurrentItem() + 1);
                    }
                    int viewUnderPosition = owner.getChildAdapterPosition(owner.findChildViewUnder(dragEvent.getX(), dragEvent.getY()));
                    if(viewUnderPosition != -1) {
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
