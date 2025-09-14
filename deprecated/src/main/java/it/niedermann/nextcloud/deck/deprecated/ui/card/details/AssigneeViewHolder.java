package it.niedermann.nextcloud.deck.deprecated.ui.card.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAssigneeBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;

public class AssigneeViewHolder extends RecyclerView.ViewHolder {
    private final ItemAssigneeBinding binding;

    @SuppressWarnings("WeakerAccess")
    public AssigneeViewHolder(ItemAssigneeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Account account, @NonNull User user, @Nullable Consumer<User> onClickListener) {
        Glide.with(binding.avatar.getContext())
                .load(account.getAvatarUrl(binding.avatar.getResources().getDimensionPixelSize(R.dimen.avatar_size), user.getUid()))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_24dp)
                .error(R.drawable.ic_person_24dp)
                .into(binding.avatar);
        if (onClickListener != null) {
            itemView.setOnClickListener((v) -> onClickListener.accept(user));
        }
    }
}