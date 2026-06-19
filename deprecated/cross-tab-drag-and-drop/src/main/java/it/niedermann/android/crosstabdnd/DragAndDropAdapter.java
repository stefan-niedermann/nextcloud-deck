package it.niedermann.android.crosstabdnd;

import androidx.annotation.NonNull;

import java.util.List;

public interface DragAndDropAdapter<Model> {

    void removeItem(int position);

    void moveItem(int fromPosition, int toPosition);

    void insertItem(Model item, int position);

    @NonNull
    List<Model> getItemList();
}
