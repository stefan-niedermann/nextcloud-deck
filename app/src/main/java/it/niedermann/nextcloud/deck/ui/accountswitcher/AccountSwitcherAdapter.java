package it.niedermann.nextcloud.deck.ui.accountswitcher;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;

public class AccountSwitcherAdapter extends RecyclerView.Adapter<AccountSwitcherViewHolder> {

    @NonNull
    private final List<Pair<Account, String>> localAccounts = new ArrayList<>();
    @NonNull
    private final Consumer<Account> onAccountClick;

    public AccountSwitcherAdapter(@NonNull Consumer<Account> onAccountClick) {
        this.onAccountClick = onAccountClick;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return localAccounts.get(position).first.getId();
    }

    @NonNull
    @Override
    public AccountSwitcherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountSwitcherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountSwitcherViewHolder holder, int position) {
        holder.bind(localAccounts.get(position).first, localAccounts.get(position).second, onAccountClick);
    }

    @Override
    public int getItemCount() {
        return localAccounts.size();
    }

    public void setAccounts(@NonNull List<Pair<Account, String>> localAccounts) {
        this.localAccounts.clear();
        this.localAccounts.addAll(localAccounts);
        notifyDataSetChanged();
    }
}
