package it.niedermann.nextcloud.deck.ui.helper.dnd;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;

public class DraggedCardLocalState {
    private FullCard draggedCard;
    private CardView draggedView;
    private CardAdapter cardAdapter;
    private int positionInCardAdapter;
    private RecyclerView.OnChildAttachStateChangeListener insertedListener = null;
    private RecyclerView recyclerView = null;
    private long currentStackId;

    public DraggedCardLocalState(FullCard draggedCard, CardView draggedView, CardAdapter cardAdapter, int positionInCardAdapter) {
        this.draggedCard = draggedCard;
        this.draggedView = draggedView;
        this.cardAdapter = cardAdapter;
        this.positionInCardAdapter = positionInCardAdapter;
    }


    void onDragStart(ViewPager2 viewPager, FragmentManager fm) {
        FullStack fullStack = ((StackAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
//        StackFragment stackFragment = ((StackFragment) ((StackAdapter) viewPager.getAdapter()).createFragment(viewPager.getCurrentItem()));

        currentStackId = fullStack.getLocalId();

        Fragment fragment = fm.findFragmentByTag("f" + viewPager.getAdapter().getItemId(viewPager.getCurrentItem()));

        // FIXME throws NullPointer
        if (fragment instanceof StackFragment) {
            recyclerView = ((StackFragment) fragment).getRecyclerView();
        } else {
            throw new IllegalArgumentException("fragment with tag f" + viewPager.getAdapter().getItemId(viewPager.getCurrentItem()) + " is not a StackFragment");
        }

    }

    void onTabChanged(ViewPager2 viewPager, FragmentManager fm) {
        if (insertedListener != null) {
            recyclerView.removeOnChildAttachStateChangeListener(insertedListener);
            insertedListener = null;
        }
        FullStack fullStack = ((StackAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        currentStackId = fullStack.getLocalId();
        Fragment fragment = fm.findFragmentByTag("f" + viewPager.getAdapter().getItemId(viewPager.getCurrentItem()));
        if (fragment instanceof StackFragment) {
            recyclerView = ((StackFragment) fragment).getRecyclerView();
        } else {
            throw new IllegalArgumentException("fragment with tag f" + viewPager.getAdapter().getItemId(viewPager.getCurrentItem()) + " is not a StackFragment");
        }
        this.cardAdapter = (CardAdapter) recyclerView.getAdapter();
    }

    long getCurrentStackId() {
        return currentStackId;
    }

    public void setCurrentStackId(long currentStackId) {
        this.currentStackId = currentStackId;
    }

    FullCard getDraggedCard() {
        return draggedCard;
    }

    public void setDraggedCard(FullCard draggedCard) {
        this.draggedCard = draggedCard;
    }

    CardView getDraggedView() {
        return draggedView;
    }

    void setDraggedView(CardView draggedView) {
        this.draggedView = draggedView;
    }

    CardAdapter getCardAdapter() {
        return cardAdapter;
    }

    public void setCardAdapter(CardAdapter cardAdapter) {
        this.cardAdapter = cardAdapter;
    }

    int getPositionInCardAdapter() {
        return positionInCardAdapter;
    }

    void setPositionInCardAdapter(int positionInCardAdapter) {
        this.positionInCardAdapter = positionInCardAdapter;
    }

    public RecyclerView.OnChildAttachStateChangeListener getInsertedListener() {
        return insertedListener;
    }

    void setInsertedListener(RecyclerView.OnChildAttachStateChangeListener insertedListener) {
        this.insertedListener = insertedListener;
    }

    RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}
