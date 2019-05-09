package it.niedermann.nextcloud.deck.ui.helper.dnd;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;

public class DraggedCardLocalState {
    private FullCard draggedCard;
    private CardView draggedView;
    private CardAdapter cardAdapter;
    private int positionInCardAdapter;
    private RecyclerView.OnChildAttachStateChangeListener insertedListener = null;
    private RecyclerView recyclerView = null;

    public DraggedCardLocalState(FullCard draggedCard, CardView draggedView, CardAdapter cardAdapter, int positionInCardAdapter) {
        this.draggedCard = draggedCard;
        this.draggedView = draggedView;
        this.cardAdapter = cardAdapter;
        this.positionInCardAdapter = positionInCardAdapter;
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

    public void onTabChanged(ViewPager viewPager, int newTabPosition){
        if (insertedListener != null) {
            recyclerView.removeOnChildAttachStateChangeListener(insertedListener);
            insertedListener = null;
        }
        this.recyclerView = ((StackAdapter) viewPager.getAdapter()).getItem(newTabPosition).getRecyclerView();
        this.cardAdapter = (CardAdapter) recyclerView.getAdapter();
    }
}
