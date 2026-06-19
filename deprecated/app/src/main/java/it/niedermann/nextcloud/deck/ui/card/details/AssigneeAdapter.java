package it.niedermann.nextcloud.deck.ui.card.details;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemAssigneeBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;

@SuppressWarnings("WeakerAccess")
public class AssigneeAdapter extends RecyclerView.Adapter<AssigneeViewHolder> {

    @Nullable
    private RecyclerView recyclerView;
    private final Account account;
    @NonNull
    private final List<User> users = new ArrayList<>();
    @NonNull
    private final Consumer<User> userClickedListener;

    AssigneeAdapter(
            @NonNull Consumer<User> userClickedListener,
            @NonNull Account account
    ) {
        super();
        this.userClickedListener = userClickedListener;
        this.account = account;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        final var id = users.get(position).getLocalId();
        return id == null ? NO_ID : id;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @NonNull
    @Override
    public AssigneeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final var context = parent.getContext();
        return new AssigneeViewHolder(ItemAssigneeBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull AssigneeViewHolder holder, int position) {
        final var user = users.get(position);
        holder.bind(account, user, userClickedListener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(@NonNull List<User> users) {
        this.users.clear();
        this.users.addAll(users);
        updateRecylcerViewVisibility();
        notifyDataSetChanged();
    }

    public void addUser(@NonNull User user) {
        this.users.add(user);
        updateRecylcerViewVisibility();
        notifyItemInserted(this.users.size());
    }

    public void removeUser(@NonNull User user) {
        final int index = this.users.indexOf(user);
        this.users.remove(user);
        updateRecylcerViewVisibility();
        notifyItemRemoved(index);
    }

    private void updateRecylcerViewVisibility() {
        if (this.recyclerView != null) {
            this.recyclerView.setVisibility(this.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        }
    }
}
