package it.niedermann.nextcloud.deck.ui.dnd;

public interface ItemMovedByDragListener<ItemModel> {
    void onItemMoved(ItemModel movedItem, long tabId, int position);
}