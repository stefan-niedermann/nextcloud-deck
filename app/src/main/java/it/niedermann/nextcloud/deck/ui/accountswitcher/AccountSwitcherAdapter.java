package it.niedermann.nextcloud.deck.ui.accountswitcher;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;

public class AccountSwitcherAdapter extends RecyclerView.Adapter<AccountSwitcherViewHolder> {

    @NonNull
    private final List<Account> localAccounts = new ArrayList<>();
    @NonNull
    private final Consumer<Account> onAccountClick;

    public AccountSwitcherAdapter(@NonNull Consumer<Account> onAccountClick) {
        this.onAccountClick = onAccountClick;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return localAccounts.get(position).getId();
    }

    @NonNull
    @Override
    public AccountSwitcherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountSwitcherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountSwitcherViewHolder holder, int position) {
        holder.bind(localAccounts.get(position), onAccountClick);
    }

    @Override
    public int getItemCount() {
        return localAccounts.size();
    }

    public void setAccounts(@NonNull List<Account> localAccounts) {
        this.localAccounts.clear();
        this.localAccounts.addAll(localAccounts);
        notifyDataSetChanged();
    }
}
