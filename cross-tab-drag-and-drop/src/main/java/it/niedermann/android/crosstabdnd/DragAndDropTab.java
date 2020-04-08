package it.niedermann.android.crosstabdnd;

import androidx.recyclerview.widget.RecyclerView;

public interface DragAndDropTab<ItemAdapter extends RecyclerView.Adapter<?> & DragAndDropAdapter<?>> {

    ItemAdapter getAdapter();

    RecyclerView getRecyclerView();
}


