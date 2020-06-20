package it.niedermann.nextcloud.deck.ui.accountswitcher;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.niedermann.android.glidesso.SingleSignOnUrl;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAccountChooseBinding;
import it.niedermann.nextcloud.deck.model.Account;

import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

public class AccountSwitcherViewHolder extends RecyclerView.ViewHolder {

    ItemAccountChooseBinding binding;

    public AccountSwitcherViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemAccountChooseBinding.bind(itemView);
    }

    public void bind(@NonNull Account account, @NonNull Consumer<Account> onAccountClick) {
        binding.accountName.setText(account.getUserName());
        binding.accountHost.setText(Uri.parse(account.getUrl()).getHost());
        Glide.with(itemView.getContext())
                .load(new SingleSignOnUrl(account.getName(), account.getAvatarUrl(dpToPx(binding.accountItemAvatar.getContext(), R.dimen.avatar_size))))
                .error(R.drawable.ic_person_grey600_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.accountItemAvatar);
        itemView.setOnClickListener((v) -> onAccountClick.accept(account));
        binding.delete.setVisibility(View.GONE);
    }
}
