package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;

public class ManageAccountAdapter extends RecyclerView.Adapter<ManageAccountViewHolder> {

    @Nullable
    private Account currentAccount = null;
    @NonNull
    private final List<Account> accounts = new ArrayList<>();
    @NonNull
    private final Consumer<Account> onAccountClick;
    @NonNull
    private final Consumer<Pair<Account, Account>> onAccountDelete;

    public ManageAccountAdapter(@NonNull Consumer<Account> onAccountClick, @NonNull Consumer<Pair<Account, Account>> onAccountDelete) {
        this.onAccountClick = onAccountClick;
        this.onAccountDelete = onAccountDelete;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return accounts.get(position).getId();
    }

    @NonNull
    @Override
    public ManageAccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageAccountViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ManageAccountViewHolder holder, int position) {
        final Account account = accounts.get(position);
        holder.bind(account, (clickedAccount) -> {
            setCurrentAccount(clickedAccount);
            onAccountClick.accept(clickedAccount);
        }, (accountToDelete -> {
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getId().equals(accountToDelete.getId())) {
                    accounts.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }

            Account newAccount = accounts.size() > 0 ? accounts.get(0) : null;
            setCurrentAccount(newAccount);
            onAccountDelete.accept(new Pair<>(accountToDelete, newAccount));
        }), currentAccount != null && currentAccount.getId().equals(account.getId()));
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void setAccounts(@NonNull List<Account> Accounts) {
        this.accounts.clear();
        this.accounts.addAll(Accounts);
        notifyDataSetChanged();
    }

    public void setCurrentAccount(@Nullable Account currentAccount) {
        this.currentAccount = currentAccount;
        notifyDataSetChanged();
    }
}
