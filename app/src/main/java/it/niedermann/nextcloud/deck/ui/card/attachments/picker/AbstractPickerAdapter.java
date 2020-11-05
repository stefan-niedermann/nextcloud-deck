package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractPickerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    protected static final int VIEW_TYPE_ITEM = 0;
    protected static final int VIEW_TYPE_ITEM_NATIVE = 1;

    @Override
    public int getItemViewType(int position) {
        return position == 0
                ? VIEW_TYPE_ITEM_NATIVE
                : VIEW_TYPE_ITEM;
    }

    /**
     * Call this method when the {@link AbstractPickerAdapter} is no longer need to free resources.
     */
    public abstract void onDestroy();
}