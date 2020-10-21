package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAccountChooseBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.sso.glide.SingleSignOnUrl;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ManageAccountViewHolder extends RecyclerView.ViewHolder {

    private ItemAccountChooseBinding binding;

    public ManageAccountViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemAccountChooseBinding.bind(itemView);
    }

    public void bind(@NonNull Account account, @NonNull Consumer<Account> onAccountClick, @Nullable Consumer<Account> onAccountDelete, boolean isCurrentAccount) {
        binding.accountName.setText(account.getUserName());
        binding.accountHost.setText(Uri.parse(account.getUrl()).getHost());
        Glide.with(itemView.getContext())
                .load(new SingleSignOnUrl(account.getName(), account.getAvatarUrl(DimensionUtil.INSTANCE.dpToPx(binding.accountItemAvatar.getContext(), R.dimen.avatar_size))))
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.accountItemAvatar);
        binding.currentAccountIndicator.setSelected(isCurrentAccount);
        itemView.setOnClickListener((v) -> onAccountClick.accept(account));
        if (onAccountDelete == null) {
            binding.delete.setVisibility(GONE);
        } else {
            binding.delete.setVisibility(VISIBLE);
            binding.delete.setOnClickListener((v) -> onAccountDelete.accept(account));
        }
        if (isCurrentAccount) {
            binding.currentAccountIndicator.setVisibility(VISIBLE);
        } else {
            binding.currentAccountIndicator.setVisibility(GONE);
        }
    }
}
