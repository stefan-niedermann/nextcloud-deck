package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;

public class ManageAccountAdapter extends RecyclerView.Adapter<ManageAccountViewHolder> {

    @Nullable
    private Account currentAccount = null;
    @NonNull
    private final List<Account> Accounts = new ArrayList<>();
    @NonNull
    private final Consumer<Account> onAccountClick;
    @Nullable
    private final Consumer<Account> onAccountDelete;

    public ManageAccountAdapter(@NonNull Consumer<Account> onAccountClick, @Nullable Consumer<Account> onAccountDelete) {
        this.onAccountClick = onAccountClick;
        this.onAccountDelete = onAccountDelete;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return Accounts.get(position).getId();
    }

    @NonNull
    @Override
    public ManageAccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageAccountViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ManageAccountViewHolder holder, int position) {
        final Account Account = Accounts.get(position);
        holder.bind(Account, (account) -> {
            setCurrentAccount(account);
            onAccountClick.accept(account);
        }, (accountToDelete -> {
            if (onAccountDelete != null) {
                for (int i = 0; i < Accounts.size(); i++) {
                    if (Accounts.get(i).getId() == accountToDelete.getId()) {
                        Accounts.remove(i);
                        notifyItemRemoved(i);
                        break;
                    }
                }
                onAccountDelete.accept(accountToDelete);
            }
        }), currentAccount != null && currentAccount.getId() == Account.getId());
    }

    @Override
    public int getItemCount() {
        return Accounts.size();
    }

    public void setAccounts(@NonNull List<Account> Accounts) {
        this.Accounts.clear();
        this.Accounts.addAll(Accounts);
        notifyDataSetChanged();
    }

    public void setCurrentAccount(@Nullable Account currentAccount) {
        this.currentAccount = currentAccount;
        notifyDataSetChanged();
    }
}
