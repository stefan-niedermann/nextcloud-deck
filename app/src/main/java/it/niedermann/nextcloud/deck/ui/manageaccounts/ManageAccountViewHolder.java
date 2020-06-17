package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAccountChooseBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.util.glide.SingleSignOnOriginHeader;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

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
                .load(new GlideUrl(account.getAvatarUrl(dpToPx(binding.accountItemAvatar.getContext(), R.dimen.avatar_size)), new SingleSignOnOriginHeader(account)))
                .error(R.drawable.ic_person_grey600_24dp)
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
