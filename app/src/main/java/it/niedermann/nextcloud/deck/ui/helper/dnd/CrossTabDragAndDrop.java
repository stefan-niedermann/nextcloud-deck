package it.niedermann.nextcloud.deck.ui.helper.dnd;

import android.graphics.Point;
import android.view.DragEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;

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



//            final RecyclerView recyclerView = (RecyclerView) cardView.getParent();
//
//            if (recyclerView == null) {
//                return true;
//            }

            StackAdapter newAdapter2 = ((StackAdapter) viewPager.getAdapter());
            StackFragment newFragment2 = newAdapter2.getItem(viewPager.getCurrentItem());
            final RecyclerView recyclerView = newFragment2.getRecyclerView();

            CardAdapter cardAdapter = (CardAdapter) recyclerView.getAdapter();
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    CardView cardView = (CardView) dragEvent.getLocalState();
                    cardView.setVisibility(View.INVISIBLE);
                    break;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    CardView cardView = (CardView) dragEvent.getLocalState();
                    Point size = new Point();
                    mainActivity.getWindowManager().getDefaultDisplay().getSize(size);
                    long now = System.currentTimeMillis();
                    if (lastSwap + msToReact < now) { // don't change Tabs so fast!
                        if (dragEvent.getX() <= pxToReact) {
                            //TODO: this one depends on the initial view the drag started from.
                            // maybe we should search all tabs rather than only the source tab
                            int oldCardPosition = recyclerView.getChildAdapterPosition(cardView);
                            int oldTabPosition = viewPager.getCurrentItem();

                            FullCard itemToMove = cardAdapter.getItem(oldCardPosition);

                            cardAdapter.removeItem(oldCardPosition);

                            viewPager.setCurrentItem(oldTabPosition - 1);

                            StackAdapter newAdapter = ((StackAdapter) viewPager.getAdapter());
                            StackFragment newFragment = newAdapter.getItem(viewPager.getCurrentItem());
                            final RecyclerView newrecyclerView = newFragment.getRecyclerView();
                            cardAdapter = (CardAdapter) newrecyclerView.getAdapter();

                            int insertedPosition = cardAdapter.addItem(itemToMove);
                            cardView.setVisibility(View.INVISIBLE);


                            newrecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                                @Override
                                public void onChildViewAttachedToWindow(@NonNull View view) {
                                    newrecyclerView.removeOnChildAttachStateChangeListener(this);
                                    CardView cardView = (CardView) view;

                                    cardView.setVisibility(View.INVISIBLE);

                                }

                                @Override
                                public void onChildViewDetachedFromWindow(@NonNull View view) {
                                }
                            });

                            cardAdapter.notifyItemInserted(insertedPosition);

                            lastSwap = now;


//                            View viewUnder = newFragment.recyclerView.findChildViewUnder(dragEvent.getX(), dragEvent.getY());
                        } else if (dragEvent.getX() >= size.x - pxToReact) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            lastSwap = now;
                        }
                    }

                    //push around the other cards
                    StackAdapter newAdapter1 = ((StackAdapter) viewPager.getAdapter());
                    StackFragment newFragment1 = newAdapter1.getItem(viewPager.getCurrentItem());
                    RecyclerView currentRecyclerView = getCurrentRecyclerView(viewPager);
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
                    CardView cardView = findInvisibleCardView(getCurrentRecyclerView(viewPager));
                    cardView.setVisibility(View.VISIBLE);
                    break;
                }
            }
            return true;
        });
    }

    private static CardView findInvisibleCardView(RecyclerView recyclerView){
        CardView foundView = null;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View view = recyclerView.getChildAt(i);

            if (view.findViewById(R.id.card).getVisibility() == View.INVISIBLE) {
                foundView = (CardView) view;
            }

        }
        return foundView;
    }

    private static RecyclerView getCurrentRecyclerView(ViewPager viewPager) {
        return ((StackAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem()).getRecyclerView();
    }
}
