package it.niedermann.nextcloud.deck.ui.card;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAutocompleteUserBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.extrawurst.UserSearchLiveData;
import it.niedermann.nextcloud.deck.util.AutoCompleteAdapter;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class UserAutoCompleteAdapter extends AutoCompleteAdapter<User> {
    @NonNull
    private Account account;
    private UserSearchLiveData liveSearchForACL;
    private LiveData<List<User>> liveData;
    private Observer<List<User>> observer;

    public UserAutoCompleteAdapter(@NonNull ComponentActivity activity, @NonNull Account account, long boardId) {
        this(activity, account, boardId, NO_CARD);
    }

    public UserAutoCompleteAdapter(@NonNull ComponentActivity activity, @NonNull Account account, long boardId, long cardId) {
        super(activity, account.getId(), boardId, cardId);
        this.account = account;
        this.liveSearchForACL = syncManager.searchUserByUidOrDisplayNameForACL();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemAutocompleteUserBinding binding;

        if (convertView != null) {
            binding = ItemAutocompleteUserBinding.bind(convertView);
        } else {
            binding = ItemAutocompleteUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }

        ViewUtil.addAvatar(binding.icon, account.getUrl(), getItem(position).getUid(), R.drawable.ic_person_grey600_24dp);
        binding.label.setText(getItem(position).getDisplayname());

        return binding.getRoot();
    }

    @Override
    public Filter getFilter() {
        return new AutoCompleteFilter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    activity.runOnUiThread(() -> {
                        final int constraintLength = constraint.toString().trim().length();
                        if (cardId == NO_CARD) {
                            liveData = constraintLength > 0
                                    ? liveSearchForACL.search(accountId, boardId, constraint.toString())
                                    : syncManager.findProposalsForUsersToAssignForACL(accountId, boardId, activity.getResources().getInteger(R.integer.max_users_suggested));
                        } else {
                            liveData = constraintLength > 0
                                    ? syncManager.searchUserByUidOrDisplayName(accountId, boardId, cardId, constraint.toString())
                                    : syncManager.findProposalsForUsersToAssign(accountId, boardId, cardId, activity.getResources().getInteger(R.integer.max_users_suggested));
                        }
                        liveData.removeObservers(activity);
                        observer = users -> {
                            users.removeAll(itemsToExclude);
                            filterResults.values = users;
                            filterResults.count = users.size();
                            publishResults(constraint, filterResults);
                        };
                        liveData.observe(activity, observer);
                    });
                }
                return filterResults;
            }
        };
    }
}
