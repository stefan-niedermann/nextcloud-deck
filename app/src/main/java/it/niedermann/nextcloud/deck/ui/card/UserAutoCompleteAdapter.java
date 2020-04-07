package it.niedermann.nextcloud.deck.ui.card;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAutocompleteDropdownBinding;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.util.AutoCompleteAdapter;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class UserAutoCompleteAdapter extends AutoCompleteAdapter<User> {

    public UserAutoCompleteAdapter(@NonNull ComponentActivity activity, long accountId, long boardId) {
        this(activity, accountId, boardId, NO_CARD);
    }

    public UserAutoCompleteAdapter(@NonNull ComponentActivity activity, long accountId, long boardId, long cardId) {
        super(activity, accountId, boardId, cardId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder<ItemAutocompleteDropdownBinding> holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            ItemAutocompleteDropdownBinding binding = ItemAutocompleteDropdownBinding.inflate(inflater, parent, false);
            holder = new ViewHolder<>(binding);
            convertView = binding.getRoot();
            convertView.setTag(holder);
        }

        try {
            SingleSignOnAccount account = SingleAccountHelper.getCurrentSingleSignOnAccount(activity);
            ViewUtil.addAvatar(
                    activity,
                    holder.binding.icon,
                    account.url,
                    getItem(position).getUid(),
                    R.drawable.ic_person_grey600_24dp
            );
        } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }

        holder.binding.label.setText(getItem(position).getDisplayname());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new AutoCompleteFilter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    activity.runOnUiThread(() -> {
                        LiveData<List<User>> liveData;
                        final int constraintLength = constraint.toString().trim().length();
                        if (cardId == NO_CARD) {
                            liveData = constraintLength > 0
                                    ? syncManager.searchUserByUidOrDisplayNameForACL(accountId, boardId, constraint.toString())
                                    : syncManager.findProposalsForUsersToAssignForACL(accountId, boardId, activity.getResources().getInteger(R.integer.max_users_suggested));
                        } else {
                            liveData = constraintLength > 0
                                    ? syncManager.searchUserByUidOrDisplayName(accountId, cardId, constraint.toString())
                                    : syncManager.findProposalsForUsersToAssign(accountId, boardId, cardId, activity.getResources().getInteger(R.integer.max_users_suggested));
                        }
                        observeOnce(liveData, activity, users -> {
                            users.removeAll(itemsToExclude);
                            filterResults.values = users;
                            filterResults.count = users.size();
                            publishResults(constraint, filterResults);
                        });
                    });
                }
                return filterResults;
            }
        };
    }
}
