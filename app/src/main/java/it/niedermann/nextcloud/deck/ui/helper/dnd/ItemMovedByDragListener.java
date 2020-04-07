package it.niedermann.nextcloud.deck.ui.helper.dnd;

public interface ItemMovedByDragListener<ItemModel> {
    void onItemMoved(ItemModel movedItem, long tabId, int position);
}