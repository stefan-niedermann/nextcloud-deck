package it.niedermann.nextcloud.deck.ui.helper.dnd;

import androidx.cardview.widget.CardView;

import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;

public class DraggedCardData {
    private FullCard draggedCard;
    private CardView draggedView;
    private CardAdapter cardAdapter;

    public DraggedCardData(FullCard draggedCard, CardView draggedView, CardAdapter cardAdapter) {
        this.draggedCard = draggedCard;
        this.draggedView = draggedView;
        this.cardAdapter = cardAdapter;
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
}
