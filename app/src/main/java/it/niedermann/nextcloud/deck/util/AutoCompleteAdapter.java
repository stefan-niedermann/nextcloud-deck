package it.niedermann.nextcloud.deck.util;

import static java.util.stream.Collectors.toList;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.repository.SyncRepository;

public abstract class AutoCompleteAdapter<ItemType extends IRemoteEntity> extends BaseAdapter implements Filterable {
    @NonNull
    private List<ItemType> itemList = new ArrayList<>();
    @NonNull
    private final List<ItemType> itemsToExclude = new ArrayList<>();
    @NonNull
    protected SyncRepository syncRepository;
    protected final Account account;
    protected final long boardId;
    protected final ReactiveLiveData<String> constraint$ = new ReactiveLiveData<>();
    private final AutoCompleteFilter filter = new AutoCompleteFilter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            constraint$.postValue(constraint == null ? "" : constraint.toString());
            return filterResults;
        }
    };

    protected AutoCompleteAdapter(@NonNull Context context, @NonNull Account account, long boardId) throws NextcloudFilesAppAccountNotFoundException {
        this.account = account;
        this.boardId = boardId;
        this.syncRepository = new SyncRepository(context, account);
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
        // Create proposals do have null as local ID
        final var localId = itemList.get(position).getLocalId();
        return localId == null ? Long.MIN_VALUE : localId;
    }

    protected List<ItemType> filterExcluded(@NonNull List<ItemType> users) {
        return users.stream().filter(this::itemIsNotInExclusionList).collect(toList());
    }

    private boolean itemIsNotInExclusionList(@NonNull ItemType item) {
        return itemsToExclude
                .stream()
                .map(IRemoteEntity::getLocalId)
                .noneMatch(idToExclude -> Objects.equals(item.getLocalId(), idToExclude));
    }

    protected static class ViewHolder<ViewBindingType extends ViewBinding> {
        public final ViewBindingType binding;

        public ViewHolder(ViewBindingType binding) {
            this.binding = binding;
        }
    }

    public abstract class AutoCompleteFilter extends Filter {
        protected final Filter.FilterResults filterResults = new Filter.FilterResults();

        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                if (!itemList.equals(results.values)) {
                    //noinspection unchecked
                    itemList = (List<ItemType>) results.values;
                }
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        private void publishResults(List<ItemType> list) {
            DeckLog.verbose("New result list", list.stream().map(IRemoteEntity::toString).collect(toList()));
            filterResults.values = list;
            filterResults.count = list.size();
            publishResults("", filterResults);
        }

        public Filter.FilterResults getFilter() {
            return filterResults;
        }
    }

    protected void publishResults(List<ItemType> list) {
        filter.publishResults(list);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void exclude(ItemType item) {
        this.itemsToExclude.add(item);
    }

    public void doNotLongerExclude(ItemType item) {
        this.itemsToExclude.remove(item);
    }
}
