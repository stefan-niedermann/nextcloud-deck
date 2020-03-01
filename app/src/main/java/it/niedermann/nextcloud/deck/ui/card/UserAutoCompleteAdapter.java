package it.niedermann.nextcloud.deck.ui.card;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAutocompleteDropdownBinding;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class UserAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<User> userList = new ArrayList<>();
    private SyncManager syncManager;
    private long accountId;
    private long boardId;
    private long cardId;
    private LifecycleOwner owner;

    public UserAutoCompleteAdapter(@NonNull LifecycleOwner owner, Activity activity, long accountId, long boardId) {
        this(owner, activity, accountId, boardId, 0L);
    }

    UserAutoCompleteAdapter(@NonNull LifecycleOwner owner, Activity activity, long accountId, long boardId, long cardId) {
        this.owner = owner;
        this.context = activity;
        this.accountId = accountId;
        this.boardId = boardId;
        this.cardId = cardId;
        syncManager = new SyncManager(activity);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ItemAutocompleteDropdownBinding binding = ItemAutocompleteDropdownBinding.inflate(inflater, parent, false);
            holder = new ViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(holder);
        }

        try {
            SingleSignOnAccount account = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
            ViewUtil.addAvatar(
                    context,
                    holder.binding.icon,
                    account.url,
                    getItem(position).getUid(),
                    R.drawable.ic_person_grey600_24dp
            );
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            DeckLog.logError(e);
        } catch (NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }

        holder.binding.label.setText(getItem(position).getDisplayname());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            final FilterResults filterResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    Objects.requireNonNull(((Fragment) owner).getActivity()).runOnUiThread(() -> {
                        LiveData<List<User>> liveData;
                        if (cardId == 0L) {
                            liveData = constraint.toString().trim().length() > 0
                                    ? syncManager.searchUserByUidOrDisplayNameForACL(accountId, boardId, constraint.toString())
                                    : syncManager.findProposalsForUsersToAssignForACL(accountId, boardId, context.getResources().getInteger(R.integer.max_users_suggested));
                        } else {
                            liveData = constraint.toString().trim().length() > 0
                                    ? syncManager.searchUserByUidOrDisplayName(accountId, cardId, constraint.toString())
                                    : syncManager.findProposalsForUsersToAssign(accountId, boardId, cardId, context.getResources().getInteger(R.integer.max_users_suggested));
                        }
                        observeOnce(liveData, owner, users -> {
                            if (users != null) {
                                filterResults.values = users;
                                filterResults.count = users.size();
                                publishResults(constraint, filterResults);
                            } else {
                                filterResults.values = new ArrayList<>();
                                filterResults.count = 0;
                            }
                        });
                    });
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    if (!userList.equals(results.values)) {
                        userList = (List<User>) results.values;
                    }
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    static class ViewHolder {
        private ItemAutocompleteDropdownBinding binding;

        ViewHolder(ItemAutocompleteDropdownBinding binding) {
            this.binding = binding;
        }
    }
}
