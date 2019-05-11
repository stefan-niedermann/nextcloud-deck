package it.niedermann.nextcloud.deck.ui.helper.dnd;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import it.niedermann.nextcloud.deck.model.full.FullCard;
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


    public void onDragStart(ViewPager viewPager){
        StackFragment stackFragment = ((StackAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        currentStackId = stackFragment.getStackId();
        recyclerView = stackFragment.getRecyclerView();

    }
    public void onTabChanged(ViewPager viewPager, int newTabPosition){
        if (insertedListener != null) {
            recyclerView.removeOnChildAttachStateChangeListener(insertedListener);
            insertedListener = null;
        }
        StackFragment stackFragment = ((StackAdapter) viewPager.getAdapter()).getItem(newTabPosition);
        currentStackId = stackFragment.getStackId();
        this.recyclerView = stackFragment.getRecyclerView();
        this.cardAdapter = (CardAdapter) recyclerView.getAdapter();

        for (int i = 0; i < cardAdapter.getCardList().size(); i++) {
            FullCard fullCard = cardAdapter.getCardList().get(i);
            if (fullCard.getLocalId().equals(draggedCard.getLocalId())){
                cardAdapter.getCardList().remove(fullCard);
                cardAdapter.notifyItemRemoved(i);
                break;
            }
        }
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
