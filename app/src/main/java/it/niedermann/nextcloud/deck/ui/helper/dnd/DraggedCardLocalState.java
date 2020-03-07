package it.niedermann.nextcloud.deck.ui.helper.dnd;

import androidx.cardview.widget.CardView;
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


    public void onDragStart(ViewPager2 viewPager) {
        FullStack fullStack = ((StackAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        StackFragment stackFragment = ((StackFragment) ((StackAdapter) viewPager.getAdapter()).createFragment(viewPager.getCurrentItem()));
        currentStackId = fullStack.getLocalId();
        // FIXME throws NullPointer
        recyclerView = stackFragment.getRecyclerView();

    }

    public void onTabChanged(ViewPager2 viewPager) {
        if (insertedListener != null) {
            recyclerView.removeOnChildAttachStateChangeListener(insertedListener);
            insertedListener = null;
        }
        FullStack fullStack = ((StackAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        StackFragment stackFragment = ((StackFragment) ((StackAdapter) viewPager.getAdapter()).createFragment(viewPager.getCurrentItem()));
        currentStackId = fullStack.getLocalId();
        // FIXME throws probably NullPointer
        this.recyclerView = stackFragment.getRecyclerView();
        this.cardAdapter = (CardAdapter) recyclerView.getAdapter();
    }

    public long getCurrentStackId() {
        return currentStackId;
    }

    public void setCurrentStackId(long currentStackId) {
        this.currentStackId = currentStackId;
    }

    public FullCard getDraggedCard() {
        return draggedCard;
    }

    public void setDraggedCard(FullCard draggedCard) {
        this.draggedCard = draggedCard;
    }

    public CardView getDraggedView() {
        return draggedView;
    }

    public void setDraggedView(CardView draggedView) {
        this.draggedView = draggedView;
    }

    public CardAdapter getCardAdapter() {
        return cardAdapter;
    }

    public void setCardAdapter(CardAdapter cardAdapter) {
        this.cardAdapter = cardAdapter;
    }

    public int getPositionInCardAdapter() {
        return positionInCardAdapter;
    }

    public void setPositionInCardAdapter(int positionInCardAdapter) {
        this.positionInCardAdapter = positionInCardAdapter;
    }

    public RecyclerView.OnChildAttachStateChangeListener getInsertedListener() {
        return insertedListener;
    }

    public void setInsertedListener(RecyclerView.OnChildAttachStateChangeListener insertedListener) {
        this.insertedListener = insertedListener;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}
