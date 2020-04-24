package it.niedermann.nextcloud.deck.ui.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

@SuppressWarnings("WeakerAccess")
public class UserFilterAdapter extends RecyclerView.Adapter<UserFilterAdapter.UserViewHolder> {
    final int avatarSize;
    @NonNull
    private final Context context;
    @NonNull
    private final Account account;
    @NonNull
    private final List<User> users = new ArrayList<>();
    @NonNull
    private final List<User> selectedUsers = new ArrayList<>();

    public UserFilterAdapter(@NonNull Context context, @NonNull Account account, @NonNull List<User> users, @NonNull List<User> selectedUsers) {
        super();
        this.account = account;
        this.context = context;
        this.users.addAll(users);
        this.selectedUsers.addAll(selectedUsers);
        setHasStableIds(true);
        notifyDataSetChanged();
        avatarSize = dpToPx(context, R.dimen.avatar_size);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).getLocalId();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_filter_user, viewGroup, false);
        return new UserViewHolder(view);
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

        // TODO Use ViewBinding
        private TextView displayName;
        private ImageView avatar;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            displayName = itemView.findViewById(R.id.displayName);
            avatar = itemView.findViewById(R.id.avatar);
        }

        void bind(final User user) {
            displayName.setText(user.getDisplayname());
            ViewUtil.addAvatar(avatar, account.getUrl(), user.getUid(), avatarSize, R.drawable.ic_person_grey600_24dp);
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