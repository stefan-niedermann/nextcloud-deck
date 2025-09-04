package it.niedermann.nextcloud.deck.ui.card;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.List;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAutocompleteUserBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.util.AutoCompleteAdapter;

public class UserAutoCompleteAdapter extends AutoCompleteAdapter<User> {

    private static final long NO_CARD = Long.MIN_VALUE;
    @NonNull
    private final Account account;

    /**
     * Use this constructor to find users to be added to the ACL of a board
     */
    public UserAutoCompleteAdapter(@NonNull ComponentActivity activity, @NonNull Account account, long boardId) throws NextcloudFilesAppAccountNotFoundException {
        this(activity, account, boardId, NO_CARD);
    }

    /**
     * Use this constructor to find users to be added to a specific card which are already in the ACL of the board
     */
    public UserAutoCompleteAdapter(@NonNull ComponentActivity activity, @NonNull Account account, long boardId, long cardId) throws NextcloudFilesAppAccountNotFoundException {
        super(activity, account, boardId);
        this.account = account;

        final ReactiveLiveData<List<User>> results$;

        constraint$
                .filter(constraint -> !TextUtils.isEmpty(constraint))
                .debounce(300)
                .observe(activity, constraint -> {
                    DeckLog.verbose("Triggering remote search");
                    syncRepository.triggerUserSearch(account, constraint);
                });

        if (cardId == NO_CARD) {
            // No card means this adapter is used for searching users for Board ACL
            results$ = constraint$.flatMap(constraint -> TextUtils.isEmpty(constraint)
                    ? userRepository.findProposalsForUsersToAssignForACL(account.getId(), boardId, activity.getResources().getInteger(R.integer.max_users_suggested))
                    : userRepository.searchUserByUidOrDisplayNameForACL(account.getId(), boardId, constraint));
        } else {
            // Card is given, so we are searching for users to assign to a card (limited to users whom the board is shared with)
            results$ = constraint$.flatMap(constraint -> TextUtils.isEmpty(constraint)
                    ? userRepository.findProposalsForUsersToAssignForCards(account.getId(), boardId, cardId, activity.getResources().getInteger(R.integer.max_users_suggested))
                    : userRepository.searchUserByUidOrDisplayNameForCards(account.getId(), boardId, cardId, constraint));
        }

        results$
                .map(this::filterExcluded)
                .distinctUntilChanged()
                .observe(activity, this::publishResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemAutocompleteUserBinding binding;

        if (convertView != null) {
            binding = ItemAutocompleteUserBinding.bind(convertView);
        } else {
            binding = ItemAutocompleteUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }

        Glide.with(binding.icon.getContext())
                .load(account.getAvatarUrl(binding.icon.getResources().getDimensionPixelSize(R.dimen.avatar_size), getItem(position).getUid()))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_24dp)
                .error(R.drawable.ic_person_24dp)
                .into(binding.icon);
        binding.label.setText(getItem(position).getDisplayname());

        return binding.getRoot();
    }
}
