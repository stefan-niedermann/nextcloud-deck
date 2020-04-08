package it.niedermann.nextcloud.deck.ui.pickstack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPickStackAccountBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.util.DimensionUtil.getAvatarDimension;

public class AccountAdapter extends ArrayAdapter<Account> {

    @NonNull
    private final LayoutInflater inflater;

    @SuppressWarnings("WeakerAccess")
    public AccountAdapter(@NonNull Context context) {
        super(context, R.layout.item_pick_stack_account);
        setDropDownViewResource(R.layout.item_pick_stack_account);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return Objects.requireNonNull(getItem(position)).getId();
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        final ItemPickStackAccountBinding binding;
        if (convertView == null) {
            binding = ItemPickStackAccountBinding.inflate(inflater, parent, false);
        } else {
            binding = ItemPickStackAccountBinding.bind(convertView);
        }

        final Account item = getItem(position);
        if (item != null) {
            binding.username.setText(item.getUserName());
            binding.instance.setText(item.getUrl());
            ViewUtil.addAvatar(binding.avatar.getContext(), binding.avatar, item.getUrl(), item.getUserName(), getAvatarDimension(binding.avatar.getContext(), R.dimen.icon_size_details), R.drawable.ic_person_grey600_24dp);
        } else {
            DeckLog.logError(new IllegalArgumentException("No item for position " + position));
        }
        return binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
