package it.niedermann.nextcloud.deck.ui.card;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAutocompleteCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.util.AutoCompleteAdapter;

public class DependentAutoCompleteAdapter extends AutoCompleteAdapter<Card> {

    private static final long NO_CARD = Long.MIN_VALUE;
    @NonNull
    private final Account account;

    /**
     * Use this constructor to find users to be added to the ACL of a board
     */
    public DependentAutoCompleteAdapter(@NonNull ComponentActivity activity, @NonNull Account account, long boardId) throws NextcloudFilesAppAccountNotFoundException {
        this(activity, account, boardId, NO_CARD);
    }

    /**
     * Use this constructor to find users to be added to a specific card which are already in the ACL of the board
     */
    public DependentAutoCompleteAdapter(@NonNull ComponentActivity activity, @NonNull Account account, long boardId, long cardId) throws NextcloudFilesAppAccountNotFoundException {
        super(activity, account, boardId);
        this.account = account;

        constraint$
                .filter(constraint -> !TextUtils.isEmpty(constraint))
                .flatMap(constraint -> {
                    if (constraint == null || constraint.trim().isEmpty()) {
                        return new MutableLiveData<>(Collections.emptyMap());
                    } else {
                        return syncRepository.searchCards(account.getId(), boardId, constraint, 5);
                    }
                })
                .map(map -> {
                    final var lists = map.values();
                    final var list = new ArrayList<FullCard>();
                    lists.forEach(list::addAll);
                    return list.stream()
                            .map(FullCard::getCard)
                            .filter(card -> !Objects.equals(card.getLocalId(), cardId))
                            .toList();
                })
                .map(this::filterExcluded)
                .distinctUntilChanged()
                .observe(activity, this::publishResults);
    }

    @Override
    public boolean isEnabled(int position) {
        final var card = getItem(position);
        return switch (card.getStatusEnum()) {
            case UP_TO_DATE, LOCAL_MOVED -> true;
            default -> false;
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemAutocompleteCardBinding binding;

        if (convertView != null) {
            binding = ItemAutocompleteCardBinding.bind(convertView);
        } else {
            binding = ItemAutocompleteCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }

        if (isEnabled(position)) {
            binding.title.setCompoundDrawables(null, null, null, null);
            binding.title.setEnabled(true);
        } else {
            binding.title.setEnabled(false);
            binding.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_sync_24dp, 0);
        }

        binding.title.setText(getItem(position).getTitle());

        return binding.getRoot();
    }
}
