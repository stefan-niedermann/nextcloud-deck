package it.niedermann.nextcloud.deck.ui.accountswitcher;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAccountChooseBinding;
import it.niedermann.nextcloud.deck.model.Account;

public class AccountSwitcherViewHolder extends RecyclerView.ViewHolder {

    ItemAccountChooseBinding binding;

    public AccountSwitcherViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemAccountChooseBinding.bind(itemView);
    }

    public void bind(@NonNull Account account, @NonNull Consumer<Account> onAccountClick) {
        binding.accountItemLabel.setText(account.getUserName());
        Glide.with(itemView.getContext())
                .load(account.getUrl() + "/index.php/avatar/" + Uri.encode(account.getUserName()) + "/64")
                .error(R.drawable.ic_person_grey600_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.accountItemAvatar);
        itemView.setOnClickListener((v) -> onAccountClick.accept(account));
        binding.delete.setVisibility(View.GONE);
    }
}
