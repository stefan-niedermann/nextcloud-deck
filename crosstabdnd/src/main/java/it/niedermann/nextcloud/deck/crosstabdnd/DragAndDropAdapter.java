package it.niedermann.nextcloud.deck.crosstabdnd;

import java.util.List;

public interface DragAndDropAdapter<Model> {

    void removeItem(int position);

    void moveItem(int fromPosition, int toPosition);

    void insertItem(Model item, int position);

    List<Model> getItemList();
}
