package it.niedermann.nextcloud.deck.ui.helper.dnd;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Objects;

import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;

import static it.niedermann.nextcloud.deck.ui.helper.dnd.DnDUtil.getStackFragment;

// API available for same-package-classes
@SuppressWarnings("WeakerAccess")
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

    protected void onDragStart(ViewPager2 viewPager, FragmentManager fm) {
        currentStackId = Objects.requireNonNull(viewPager.getAdapter()).getItemId(viewPager.getCurrentItem());
        recyclerView = getStackFragment(fm, currentStackId).getRecyclerView();
    }

    protected void onTabChanged(ViewPager2 viewPager, FragmentManager fm) {
        if (insertedListener != null) {
            recyclerView.removeOnChildAttachStateChangeListener(insertedListener);
            insertedListener = null;
        }
        currentStackId = Objects.requireNonNull(viewPager.getAdapter()).getItemId(viewPager.getCurrentItem());
        recyclerView = getStackFragment(fm, currentStackId).getRecyclerView();
        this.cardAdapter = (CardAdapter) recyclerView.getAdapter();
    }

    protected long getCurrentStackId() {
        return currentStackId;
    }

    protected FullCard getDraggedCard() {
        return draggedCard;
    }

    protected CardView getDraggedView() {
        return draggedView;
    }

    protected void setDraggedView(CardView draggedView) {
        this.draggedView = draggedView;
    }

    protected CardAdapter getCardAdapter() {
        return cardAdapter;
    }

    protected int getPositionInCardAdapter() {
        return positionInCardAdapter;
    }

    protected void setPositionInCardAdapter(int positionInCardAdapter) {
        this.positionInCardAdapter = positionInCardAdapter;
    }

    protected void setInsertedListener(RecyclerView.OnChildAttachStateChangeListener insertedListener) {
        this.insertedListener = insertedListener;
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

}
