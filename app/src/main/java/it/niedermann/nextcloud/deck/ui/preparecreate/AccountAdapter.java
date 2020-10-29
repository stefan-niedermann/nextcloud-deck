package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.net.URL;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPrepareCreateAccountBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.sso.glide.SingleSignOnUrl;

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

        final Account item = getItem(position);
        if (item != null) {
            binding.username.setText(item.getUserName());
            try {
                binding.instance.setText(new URL(item.getUrl()).getHost());
            } catch (Throwable t) {
                binding.instance.setText(item.getUrl());
            }

            Glide.with(getContext())
                    .load(new SingleSignOnUrl(item.getName(), item.getAvatarUrl(DimensionUtil.INSTANCE.dpToPx(binding.avatar.getContext(), R.dimen.icon_size_details))))
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .error(R.drawable.ic_baseline_account_circle_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.avatar);
        } else {
            DeckLog.logError(new IllegalArgumentException("No item for position " + position));
        }
        return binding.getRoot();
    }
}
