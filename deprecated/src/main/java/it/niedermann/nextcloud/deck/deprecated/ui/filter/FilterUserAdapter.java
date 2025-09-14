package it.niedermann.nextcloud.deck.deprecated.ui.filter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemFilterUserBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.Themed;

@SuppressWarnings("WeakerAccess")
public class FilterUserAdapter extends RecyclerView.Adapter<FilterUserAdapter.UserViewHolder> {
    @NonNull
    private final Account account;
    @Nullable
    private static final User NOT_ASSIGNED = null;
    @NonNull
    private final List<User> users = new ArrayList<>();
    @NonNull
    private final List<User> selectedUsers = new ArrayList<>();
    @Nullable
    private final SelectionListener<User> selectionListener;
    @ColorInt
    private final int color;

    public FilterUserAdapter(@NonNull Account account, @NonNull Collection<User> users, @NonNull Collection<User> selectedUsers, boolean noAssignedUser, @Nullable SelectionListener<User> selectionListener, @ColorInt int color) {
        super();
        this.account = account;
        this.users.add(NOT_ASSIGNED);
        this.users.addAll(users);
        if (noAssignedUser) {
            this.selectedUsers.add(NOT_ASSIGNED);
        }
        this.selectedUsers.addAll(selectedUsers);
        this.selectionListener = selectionListener;
        this.color = color;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        @Nullable final User user = users.get(position);
        return user == null ? -1L : user.getLocalId();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(ItemFilterUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder viewHolder, int position) {
        if (position == 0) {
            viewHolder.bindNotAssigned();
        } else {
            viewHolder.bind(users.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements Themed {
        private final ItemFilterUserBinding binding;

        UserViewHolder(@NonNull ItemFilterUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull final User user) {
            binding.title.setText(user.getDisplayname());
            Glide.with(binding.avatar.getContext())
                    .load(account.getAvatarUrl(binding.avatar.getResources().getDimensionPixelSize(R.dimen.avatar_size), user.getUid()))
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_person_24dp)
                    .error(R.drawable.ic_person_24dp)
                    .into(binding.avatar);
            itemView.setSelected(selectedUsers.contains(user));
            applyTheme(color);
            bindClickListener(user);
        }

        public void bindNotAssigned() {
            binding.title.setText(itemView.getContext().getString(R.string.simple_unassigned));
            Glide.with(itemView.getContext())
                    .load(R.drawable.ic_block_24)
                    .into(binding.avatar);
            itemView.setSelected(selectedUsers.contains(NOT_ASSIGNED));
            applyTheme(color);
            bindClickListener(NOT_ASSIGNED);
        }

        private void bindClickListener(@Nullable User user) {
            itemView.setOnClickListener(view -> {
                if (selectedUsers.contains(user)) {
                    selectedUsers.remove(user);
                    itemView.setSelected(false);
                    if (selectionListener != null) {
                        selectionListener.onItemDeselected(user);
                    }
                } else {
                    selectedUsers.add(user);
                    itemView.setSelected(true);
                    if (selectionListener != null) {
                        selectionListener.onItemSelected(user);
                    }
                }
            });
        }

        @Override
        public void applyTheme(int color) {
            final var utils = ThemeUtils.of(color, itemView.getContext());
            utils.deck.themeSelectedCheck(binding.selectedCheck.getContext(), binding.selectedCheck.getDrawable());
        }
    }
}