package it.niedermann.nextcloud.deck.util;

import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public abstract class AutoCompleteAdapter<ItemType> extends BaseAdapter implements Filterable {
    public static final long NO_CARD = Long.MIN_VALUE;
    public static final long ITEM_CREATE = Long.MIN_VALUE;
    @NonNull
    protected final ComponentActivity activity;
    @NonNull
    protected List<ItemType> itemList = new ArrayList<>();
    @NonNull
    protected List<ItemType> itemsToExclude = new ArrayList<>();
    @NonNull
    protected SyncManager syncManager;
    protected final long accountId;
    protected final long boardId;
    protected final long cardId;
    @NonNull
    protected final LayoutInflater inflater;

    protected AutoCompleteAdapter(@NonNull ComponentActivity activity, long accountId, long boardId, long cardId) {
        this(activity, accountId, boardId, cardId, new ArrayList<>());
    }

    protected AutoCompleteAdapter(@NonNull ComponentActivity activity, long accountId, long boardId, long cardId, @NonNull List<ItemType> itemsToExclude) {
        this.activity = activity;
        this.accountId = accountId;
        this.boardId = boardId;
        this.cardId = cardId;
        this.itemsToExclude.addAll(itemsToExclude);
        this.syncManager = new SyncManager(activity);
        this.inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public ItemType getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected static class ViewHolder<ViewBindingType extends ViewBinding> {
        public ViewBindingType binding;

        public ViewHolder(ViewBindingType binding) {
            this.binding = binding;
        }
    }

    public abstract class AutoCompleteFilter extends Filter {
        protected final Filter.FilterResults filterResults = new Filter.FilterResults();

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                if (!itemList.equals(results.values)) {
                    itemList = (List<ItemType>) results.values;
                }
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    public void exclude(ItemType item) {
        this.itemsToExclude.add(item);
    }

    public void include(ItemType item) {
        this.itemsToExclude.remove(item);
    }
}
