package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPrepareCreateAccountBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.util.DimensionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class AccountAdapter extends AbstractAdapter<Account> {

    @SuppressWarnings("WeakerAccess")
    public AccountAdapter(@NonNull Context context) {
        super(context, R.layout.item_prepare_create_account);
    }

    @Override
    protected long getItemId(@NonNull Account item) {
        return item.getId();
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        final ItemPrepareCreateAccountBinding binding;
        if (convertView == null) {
            binding = ItemPrepareCreateAccountBinding.inflate(inflater, parent, false);
        } else {
            binding = ItemPrepareCreateAccountBinding.bind(convertView);
        }

        final Account item = getItem(position);
        if (item != null) {
            binding.username.setText(item.getUserName());
            binding.instance.setText(item.getUrl());
            ViewUtil.addAvatar(binding.avatar, item.getUrl(), item.getUserName(), DimensionUtil.dpToPx(binding.avatar.getContext(), R.dimen.icon_size_details), R.drawable.ic_person_grey600_24dp);
        } else {
            DeckLog.logError(new IllegalArgumentException("No item for position " + position));
        }
        return binding.getRoot();
    }
}
