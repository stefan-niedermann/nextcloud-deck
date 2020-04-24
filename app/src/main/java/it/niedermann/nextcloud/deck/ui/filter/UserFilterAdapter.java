package it.niedermann.nextcloud.deck.ui.filter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemFilterUserBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.util.ViewUtil;

@SuppressWarnings("WeakerAccess")
public class UserFilterAdapter extends RecyclerView.Adapter<UserFilterAdapter.UserViewHolder> {
    @Px
    final int avatarSize;
    @NonNull
    private final Account account;
    @NonNull
    private final List<User> users = new ArrayList<>();
    @NonNull
    private final List<User> selectedUsers = new ArrayList<>();

    public UserFilterAdapter(@Px int avatarSize, @NonNull Account account, @NonNull List<User> users, @NonNull List<User> selectedUsers) {
        super();
        this.avatarSize = avatarSize;
        this.account = account;
        this.users.addAll(users);
        this.selectedUsers.addAll(selectedUsers);
        setHasStableIds(true);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).getLocalId();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(ItemFilterUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder viewHolder, int position) {
        viewHolder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public List<User> getSelected() {
        return selectedUsers;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private ItemFilterUserBinding binding;

        UserViewHolder(@NonNull ItemFilterUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final User user) {
            binding.displayName.setText(user.getDisplayname());
            ViewUtil.addAvatar(binding.avatar, account.getUrl(), user.getUid(), avatarSize, R.drawable.ic_person_grey600_24dp);
            itemView.setSelected(selectedUsers.contains(user));

            itemView.setOnClickListener(view -> {
                if (selectedUsers.contains(user)) {
                    selectedUsers.remove(user);
                    itemView.setSelected(false);
                } else {
                    selectedUsers.add(user);
                    itemView.setSelected(true);
                }
            });
        }
    }
}