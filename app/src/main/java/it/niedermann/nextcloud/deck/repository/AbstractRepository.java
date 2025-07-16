package it.niedermann.nextcloud.deck.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.remote.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.remote.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import it.niedermann.nextcloud.deck.repository.sync.SyncScheduler;
import it.niedermann.nextcloud.deck.shared.SharedExecutors;


public abstract class AbstractRepository {

    private static final ConcurrentHashMap<String, DataPropagationHelper> data_propagation_helpers = new ConcurrentHashMap<>(1);

    protected final Context context;
    protected final ConnectivityUtil connectivityUtil;
    protected final DataBaseAdapter dataBaseAdapter;
    protected final SyncScheduler syncScheduler;
    protected final ExecutorService workExecutor;
    protected final ExecutorService dbReadHighPriorityExecutor;
    protected final ExecutorService dbWriteHighPriorityExecutor;
    protected final ExecutorService dbReadLowPriorityExecutor;
    protected final ExecutorService dbWriteLowPriorityExecutor;

    protected AbstractRepository(@NonNull Context context) {
        this(context, new ConnectivityUtil(context));
    }

    protected AbstractRepository(@NonNull Context context, @NonNull ConnectivityUtil connectivityUtil) {
        this(context,
                connectivityUtil,
                new DataBaseAdapter(context.getApplicationContext()),
                new SyncScheduler.Factory(context).create(),
                SharedExecutors.getCPUExecutor(),
                SharedExecutors.getIoDbReadHighPriority(),
                SharedExecutors.getIoDbWriteHighPriority(),
                SharedExecutors.getIoDbReadLowPriority(),
                SharedExecutors.getIoDbReadLowPriority());
    }

    protected AbstractRepository(@NonNull Context context,
                                 @NonNull ConnectivityUtil connectivityUtil,
                                 @NonNull DataBaseAdapter databaseAdapter,
                                 @NonNull SyncScheduler syncScheduler,
                                 @NonNull ExecutorService workExecutor,
                                 @NonNull ExecutorService dbReadHighPriorityExecutor,
                                 @NonNull ExecutorService dbWriteHighPriorityExecutor,
                                 @NonNull ExecutorService dbReadLowPriorityExecutor,
                                 @NonNull ExecutorService dbWriteLowPriorityExecutor) {
        this.context = context.getApplicationContext();
        this.connectivityUtil = connectivityUtil;
        this.dataBaseAdapter = databaseAdapter;
        this.syncScheduler = syncScheduler;
        this.workExecutor = workExecutor;
        this.dbReadHighPriorityExecutor = dbReadHighPriorityExecutor;
        this.dbWriteHighPriorityExecutor = dbWriteHighPriorityExecutor;
        this.dbReadLowPriorityExecutor = dbReadLowPriorityExecutor;
        this.dbWriteLowPriorityExecutor = dbWriteLowPriorityExecutor;
    }

    protected DataPropagationHelper getDataPropagationHelper(@NonNull Account account) {
        return data_propagation_helpers.computeIfAbsent(account.getName(), accountName -> {
            try {
                final SingleSignOnAccount ssoAccount = AccountImporter.getSingleSignOnAccount(context, account.getName());
                final var serverAdapter = new ServerAdapter(context.getApplicationContext(), ssoAccount, connectivityUtil);
                return new DataPropagationHelper(
                        serverAdapter,
                        new DataBaseAdapter(context.getApplicationContext()),
                        SharedExecutors.getIoDbWriteHighPriority());

            } catch (NextcloudFilesAppAccountNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
