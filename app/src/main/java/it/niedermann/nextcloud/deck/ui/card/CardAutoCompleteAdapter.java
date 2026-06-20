package it.niedermann.nextcloud.deck.ui.card;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.ArrayList;

import it.niedermann.nextcloud.deck.databinding.ItemAutocompleteCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.util.AutoCompleteAdapter;

public class CardAutoCompleteAdapter extends AutoCompleteAdapter<Card> {

    private static final long NO_CARD = Long.MIN_VALUE;
    @NonNull
    private final Account account;

    /**
     * Use this constructor to find users to be added to the ACL of a board
     */
    public CardAutoCompleteAdapter(@NonNull ComponentActivity activity, @NonNull Account account, long boardId) throws NextcloudFilesAppAccountNotFoundException {
        this(activity, account, boardId, NO_CARD);
    }

    /**
     * Use this constructor to find users to be added to a specific card which are already in the ACL of the board
     */
    public CardAutoCompleteAdapter(@NonNull ComponentActivity activity, @NonNull Account account, long boardId, long cardId) throws NextcloudFilesAppAccountNotFoundException {
        super(activity, account, boardId);
        this.account = account;

        constraint$
                .filter(constraint -> !TextUtils.isEmpty(constraint))
                .flatMap(constraint -> syncRepository.searchCards(account.getId(), boardId, constraint, 5))
                .map(map -> {
                    final var lists = map.values();
                    final var list = new ArrayList<FullCard>();
                    lists.forEach(list::addAll);
                    return list.stream().map(FullCard::getCard).toList();
                })
                .map(this::filterExcluded)
                .distinctUntilChanged()
                .observe(activity, this::publishResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemAutocompleteCardBinding binding;

        if (convertView != null) {
            binding = ItemAutocompleteCardBinding.bind(convertView);
        } else {
            binding = ItemAutocompleteCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }


        binding.title.setText(getItem(position).getTitle());

        return binding.getRoot();
    }
}
