package it.niedermann.nextcloud.deck.repository;

import static java.util.concurrent.CompletableFuture.runAsync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.LastSyncUtil;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import it.niedermann.nextcloud.deck.repository.sync.SyncScheduler;

public class AccountRepository extends AbstractRepository {

    public AccountRepository(@NonNull Context context) {
        super(context);
    }

    protected AccountRepository(@NonNull Context context,
                                @NonNull ConnectivityUtil connectivityUtil,
                                @NonNull DataBaseAdapter databaseAdapter,
                                @NonNull SyncScheduler syncScheduler,
                                @NonNull ExecutorService dbReadHighPriorityExecutor,
                                @NonNull ExecutorService dbWriteHighPriorityExecutor,
                                @NonNull ExecutorService dbReadLowPriorityExecutor,
                                @NonNull ExecutorService dbWriteLowPriorityExecutor) {
        super(context,
                connectivityUtil,
                databaseAdapter,
                syncScheduler,
                dbReadHighPriorityExecutor,
                dbWriteHighPriorityExecutor,
                dbReadLowPriorityExecutor,
                dbWriteLowPriorityExecutor);
    }

    public LiveData<Account> importAccount(@NonNull String accountName,
                                           @NonNull String url,
                                           @NonNull String userName) {
        return new MutableLiveData<>();
    }

    public LiveData<Boolean> hasAccounts() {
        return LiveDataReactiveStreams.fromPublisher(dataBaseAdapter.hasAnyAccounts());
    }

    public LiveData<Account> readAccount(long id) {
        return dataBaseAdapter.readAccount(id);
    }

    public LiveData<Account> readAccount(@Nullable String name) {
        return dataBaseAdapter.readAccount(name);
    }

    public LiveData<List<Account>> readAccounts() {
        return dataBaseAdapter.readAccounts();
    }

    public List<Account> readAccountsDirectly() {
        return dataBaseAdapter.getAllAccountsDirectly();
    }

    public Account readAccountDirectly(@Nullable String name) {
        return dataBaseAdapter.readAccountDirectly(name);
    }

    public Account readAccountDirectly(long id) {
        return dataBaseAdapter.readAccountDirectly(id);
    }

    public CompletableFuture<Void> deleteAccount(long id) {
        return runAsync(() -> dataBaseAdapter.runInTransaction(() -> {
            dataBaseAdapter.saveNeighbourOfAccount(id);
            dataBaseAdapter.removeCurrentBoardId(id);
            dataBaseAdapter.deleteAccount(id);
            LastSyncUtil.resetLastSyncDate(id);
        }), dbWriteHighPriorityExecutor);
    }

    public void createAccount(@NonNull Account account, @NonNull IResponseCallback<Account> callback) {
        dbReadHighPriorityExecutor.submit(() -> {
            try {
                callback.onResponse(dataBaseAdapter.createAccountDirectly(account), IResponseCallback.EMPTY_HEADERS);
            } catch (Throwable t) {
                callback.onError(t);
            }
        });
    }
}
