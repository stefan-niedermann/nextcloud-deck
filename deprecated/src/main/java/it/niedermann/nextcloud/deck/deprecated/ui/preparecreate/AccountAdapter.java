package it.niedermann.nextcloud.deck.deprecated.ui.preparecreate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.net.URL;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPrepareCreateAccountBinding;
import it.niedermann.nextcloud.deck.deprecated.util.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;

public class AccountAdapter extends AbstractAdapter<Account> {

    @SuppressWarnings("WeakerAccess")
    public AccountAdapter(@NonNull Context context) {
        super(context, R.layout.item_prepare_create_account);
    }

    @Override
    protected long getItemId(@NonNull Account item) {
        return item.getId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ItemPrepareCreateAccountBinding binding;
        if (convertView == null) {
            binding = ItemPrepareCreateAccountBinding.inflate(inflater, parent, false);
        } else {
            binding = ItemPrepareCreateAccountBinding.bind(convertView);
        }

        final var account = getItem(position);
        if (account != null) {
            binding.username.setText(account.getUserDisplayName());
            try {
                binding.instance.setText(new URL(account.getUrl()).getHost());
            } catch (Throwable t) {
                binding.instance.setText(account.getUrl());
            }

            Glide.with(getContext())
                    .load(account.getAvatarUrl(binding.avatar.getResources().getDimensionPixelSize(R.dimen.avatar_size)))
                    .placeholder(R.drawable.ic_account_circle_24)
                    .error(R.drawable.ic_account_circle_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.avatar);
        } else {
            DeckLog.logError(new IllegalArgumentException("No item for position " + position));
        }
        return binding.getRoot();
    }
}
