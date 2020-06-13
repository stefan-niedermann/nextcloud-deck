package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAccountChooseBinding;
import it.niedermann.nextcloud.deck.model.Account;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ManageAccountViewHolder extends RecyclerView.ViewHolder {

    private ItemAccountChooseBinding binding;

    public ManageAccountViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemAccountChooseBinding.bind(itemView);
    }

    public void bind(@NonNull Account localAccount, @NonNull Consumer<Account> onAccountClick, @Nullable Consumer<Account> onAccountDelete, boolean isCurrentAccount) {
        binding.accountItemLabel.setText(localAccount.getUserName());
        Glide.with(itemView.getContext())
                .load(localAccount.getUrl() + "/index.php/avatar/" + Uri.encode(localAccount.getUserName()) + "/64")
                .error(R.drawable.ic_person_grey600_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.accountItemAvatar);
        itemView.setOnClickListener((v) -> onAccountClick.accept(localAccount));
        if (onAccountDelete == null) {
            binding.delete.setVisibility(GONE);
        } else {
            binding.delete.setVisibility(VISIBLE);
            binding.delete.setOnClickListener((v) -> onAccountDelete.accept(localAccount));
        }
        if (isCurrentAccount) {
            binding.currentAccountIndicator.setVisibility(VISIBLE);
            // TODO
//            applyBrandToLayerDrawable((LayerDrawable) binding.currentAccountIndicator.getDrawable(), R.id.area, localAccount.getColor());
        } else {
            binding.currentAccountIndicator.setVisibility(GONE);
        }
    }
}
