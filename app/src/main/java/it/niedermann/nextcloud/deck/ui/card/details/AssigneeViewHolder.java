package it.niedermann.nextcloud.deck.ui.card.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAssigneeBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class AssigneeViewHolder extends RecyclerView.ViewHolder {
    private ItemAssigneeBinding binding;

    @SuppressWarnings("WeakerAccess")
    public AssigneeViewHolder(ItemAssigneeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Account account, @NonNull User user, @Nullable Consumer<User> onClickListener) {
        ViewUtil.addAvatar(binding.avatar, account.getUrl(), user.getUid(), R.drawable.ic_person_grey600_24dp);
        if(onClickListener != null) {
            itemView.setOnClickListener((v) -> onClickListener.accept(user));
        }
    }
}