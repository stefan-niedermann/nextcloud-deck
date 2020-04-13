package it.niedermann.android.crosstabdnd;

public interface ItemMovedByDragListener<ItemModel> {
    void onItemMoved(ItemModel movedItem, long tabId, int position);
}