package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.graphics.Point;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

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

    public void register(final MainActivity mainActivity, final ViewPager viewPager) {
        viewPager.setOnDragListener((View v, DragEvent dragEvent) -> {
                DeckLog.log("--- Drag 'n' Drop: äckschn "  + dragEvent.getAction() + "");

            CardView cardView = (CardView) dragEvent.getLocalState();


//            final RecyclerView recyclerView = (RecyclerView) cardView.getParent();
//
//            if (recyclerView == null) {
//                return true;
//            }

            StackAdapter newAdapter2 = ((StackAdapter) viewPager.getAdapter());
            StackFragment newFragment2 = newAdapter2.getItem(viewPager.getCurrentItem());
            final RecyclerView recyclerView = newFragment2.recyclerView;

            CardAdapter cardAdapter = (CardAdapter) recyclerView.getAdapter();
            DeckLog.log("--- Drag 'n' Drop: event alive" );
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_LOCATION:
                    Point size = new Point();
                    mainActivity.getWindowManager().getDefaultDisplay().getSize(size);
                    long now = System.currentTimeMillis();
                    if (lastSwap + msToReact < now) { // don't change Tabs so fast!
                        if (dragEvent.getX() <= pxToReact) {
                            int oldCardPosition = recyclerView.getChildAdapterPosition(cardView);
                            int oldTabPosition = viewPager.getCurrentItem();
                            DeckLog.log("--- Drag 'n' Drop: " + "Old Card index: " + oldCardPosition + " | Old Tab: " + oldTabPosition);

                            FullCard itemToMove = cardAdapter.getItem(oldCardPosition);
                            DeckLog.log("--- Drag 'n' Drop: " + "Card: " + itemToMove.card.getTitle());

                            DeckLog.log("--- Drag 'n' Drop: " + "Remove card from position " + oldCardPosition);
                            cardAdapter.removeItem(oldCardPosition);

                            DeckLog.log("--- Drag 'n' Drop: " + "Select Tab " + (oldTabPosition - 1));
                            viewPager.setCurrentItem(oldTabPosition - 1);

                            StackAdapter newAdapter = ((StackAdapter) viewPager.getAdapter());
                            StackFragment newFragment = newAdapter.getItem(viewPager.getCurrentItem());
                            DeckLog.log("--- Drag 'n' Drop: " + "new stackfragment id: " + newFragment.getStackId());
                            final RecyclerView newrecyclerView = newFragment.recyclerView;
                            cardAdapter = (CardAdapter) newrecyclerView.getAdapter();

                            int insertedPosition = cardAdapter.addItem(itemToMove);
                            DeckLog.log("--- Drag 'n' Drop: " + "Inserted on position " + insertedPosition);
                            cardView.setVisibility(View.INVISIBLE);


                            newrecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                                @Override
                                public void onChildViewAttachedToWindow(@NonNull View view) {
                                    newrecyclerView.removeOnChildAttachStateChangeListener(this);
                                    CardView cardView = (CardView) view;

                                    DeckLog.log("--- Drag 'n' Drop: " + cardView + "");
                                    cardView.setVisibility(View.INVISIBLE);

//                                    ClipData dragData = ClipData.newPlainText("TEST", "TEST2");
//                                    cardView.startDrag(dragData,  // the data to be dragged
//                                            new View.DragShadowBuilder(){
//                                                @Override
//                                                public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
//                                                    outShadowSize.set(1,1);
//                                                    outShadowTouchPoint.set(0,0);
//                                                }
//                                            },  // the drag shadow builder
//                                            cardView,      // no need to use local data
//                                            0          // flags (not currently used, set to 0)
//                                    );
//                                    cardView.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onChildViewDetachedFromWindow(@NonNull View view) {

                                }
                            });

                            cardAdapter.notifyItemInserted(insertedPosition);

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
                    StackAdapter newAdapter1 = ((StackAdapter) viewPager.getAdapter());
                    StackFragment newFragment1 = newAdapter1.getItem(viewPager.getCurrentItem());
                    final RecyclerView newrecyclerView1 = newFragment1.recyclerView;

                    View viewUnder = newrecyclerView1.findChildViewUnder(dragEvent.getX(), dragEvent.getY());

                    CardView newCardViewToSearch = null;
                    DeckLog.log("--- Drag 'n' Drop: Card -----------------------------------------------------------");
                    for (int i = 0; i < newrecyclerView1.getChildCount(); i++) {
                        View view = newrecyclerView1.getChildAt(i);

                        DeckLog.log("--- Drag 'n' Drop: Card:" + ((TextView)view.findViewById(R.id.card_title)).getText());
                        if (view.findViewById(R.id.card).getVisibility()==View.INVISIBLE) {
                            DeckLog.log("--- Drag 'n' Drop: Card Found:" + ((TextView)view.findViewById(R.id.card_title)).getText());
                            newCardViewToSearch = (CardView) view;
                         }

                    }

                    if (newCardViewToSearch == null) {
                        return true;
                    }
                    DeckLog.log("--- Drag 'n' Drop: " + "anzahl elemente in recyclerview: " + newrecyclerView1.getChildCount());

                    if (viewUnder != null) {
                        DeckLog.log("--- +++ " + viewUnder.getClass());
                        int viewUnderPosition = newrecyclerView1.getChildAdapterPosition(viewUnder);
                        if (viewUnderPosition != -1) {
                            Objects.requireNonNull(cardAdapter).moveItem(newrecyclerView1.getChildLayoutPosition(newCardViewToSearch), viewUnderPosition);
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
