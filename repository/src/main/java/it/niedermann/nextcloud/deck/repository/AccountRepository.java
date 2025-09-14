package it.niedermann.nextcloud.deck.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.repository.sync.SyncScheduler;
import it.niedermann.nextcloud.deck.shared.model.Account;

public class AccountRepository extends AbstractRepository {

    public AccountRepository(@NonNull Context context) {
        super(context);
    }

    protected AccountRepository(@NonNull Context context,
                                @NonNull DataBaseAdapter databaseAdapter,
                                @NonNull SyncScheduler syncScheduler,
                                @NonNull ExecutorService dbReadHighPriorityExecutor,
                                @NonNull ExecutorService dbWriteHighPriorityExecutor,
                                @NonNull ExecutorService dbReadLowPriorityExecutor,
                                @NonNull ExecutorService dbWriteLowPriorityExecutor) {
        super(context,
                databaseAdapter,
                syncScheduler,
                dbReadHighPriorityExecutor,
                dbWriteHighPriorityExecutor,
                dbReadLowPriorityExecutor,
                dbWriteLowPriorityExecutor);
    }

    public LiveData<Account> importAccount(@NonNull String accountName,
                                           @NonNull String url,
                                           @NonNull String userName,
                                           @NonNull String token) {
        return new MutableLiveData<>();
    }

    public LiveData<Boolean> hasAccounts() {
        return LiveDataReactiveStreams.fromPublisher(dataBaseAdapter.hasAnyAccounts());
    }
}
