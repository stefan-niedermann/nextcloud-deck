package it.niedermann.nextcloud.deck.crosstabdnd;

public interface ItemMovedByDragListener<ItemModel> {
    void onItemMoved(ItemModel movedItem, long tabId, int position);
}