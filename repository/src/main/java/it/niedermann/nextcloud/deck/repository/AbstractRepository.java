package it.niedermann.nextcloud.deck.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.repository.sync.SyncScheduler;


public abstract class AbstractRepository {

    protected final Context context;
    protected final DataBaseAdapter dataBaseAdapter;
    protected final SyncScheduler syncScheduler;
    protected final ExecutorService dbReadHighPriorityExecutor;
    protected final ExecutorService dbWriteHighPriorityExecutor;
    protected final ExecutorService dbReadLowPriorityExecutor;
    protected final ExecutorService dbWriteLowPriorityExecutor;

    private static final PriorityThreadFactory PRIORITY_THREAD_FACTORY = new PriorityThreadFactory();

    protected AbstractRepository(@NonNull Context context) {
        this(context,
                DataBaseAdapter.getInstance(context.getApplicationContext()),
                new SyncScheduler.Factory(context).create(),
                new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MAX_PRIORITY)),
                new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MAX_PRIORITY)),
                new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MAX_PRIORITY)),
                new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MAX_PRIORITY)));
    }

    private AbstractRepository(@NonNull Context context,
                                 @NonNull DataBaseAdapter databaseAdapter,
                                 @NonNull SyncScheduler syncScheduler,
                                 @NonNull ExecutorService dbReadHighPriorityExecutor,
                                 @NonNull ExecutorService dbWriteHighPriorityExecutor,
                                 @NonNull ExecutorService dbReadLowPriorityExecutor,
                                 @NonNull ExecutorService dbWriteLowPriorityExecutor) {
        this.context = context.getApplicationContext();
        this.dataBaseAdapter = databaseAdapter;
        this.syncScheduler = syncScheduler;
        this.dbReadHighPriorityExecutor = dbReadHighPriorityExecutor;
        this.dbWriteHighPriorityExecutor = dbWriteHighPriorityExecutor;
        this.dbReadLowPriorityExecutor = dbReadLowPriorityExecutor;
        this.dbWriteLowPriorityExecutor = dbWriteLowPriorityExecutor;
    }

//    /// @param account                   the [Account] the entity to create belongs to
//    /// @param entity                    the [IRemoteEntity] to create
//    /// @param provider                  the [AbstractSyncDataProvider] for this entity type
//    /// @param serverResponseInterceptor apply additional logic when the server response has arrived, before the result gets written to the database
//    /// @return A [CompletableFuture] that will be completed successfully when the given entity was created in the database.
//    /// Whether or not the synchronization was successful or happened at all does not matter.
//    protected <T extends IRemoteEntity> CompletableFuture<T> createEntityAndScheduleSync(@NonNull Account account,
//                                                                                         @NonNull T entity,
//                                                                                         @NonNull AbstractSyncDataProvider<T> provider,
//                                                                                         @Nullable BiConsumer<T, T> serverResponseInterceptor) {
//        return createEntity(account, entity, provider)
//                .thenApplyAsync(createdEntity -> {
//                    syncScheduler.pushSingleEntity(account, () -> pushCreatedEntity(account, entity, provider, serverResponseInterceptor));
//                    return createdEntity;
//                });
//    }
//
//    /// @param account                   the [Account] the entity to create belongs to
//    /// @param entity                    the [IRemoteEntity] to create
//    /// @param provider                  the [AbstractSyncDataProvider] for this entity type
//    /// @param serverResponseInterceptor apply additional logic when the server response has arrived, before the result gets written to the database
//    /// @return A [CompletableFuture] that will be completed successfully when the given entity was created in the database and successfully synchronized to the server.
//    protected <T extends IRemoteEntity> CompletableFuture<T> createEntityAndRequireSync(@NonNull Account account,
//                                                                                        @NonNull T entity,
//                                                                                        @NonNull AbstractSyncDataProvider<T> provider,
//                                                                                        @Nullable BiConsumer<T, T> serverResponseInterceptor) {
//        return createEntity(account, entity, provider)
//                .thenComposeAsync(pushedEntity -> pushCreatedEntity(account, entity, provider, serverResponseInterceptor));
//    }
//
//    private <T extends IRemoteEntity> CompletableFuture<T> createEntity(@NonNull Account account,
//                                                                        @NonNull T entity,
//                                                                        @NonNull AbstractSyncDataProvider<T> provider) {
//        return runAsync(() -> entity.setStatus(DBStatus.LOCAL_EDITED.getId()))
//                .thenApplyAsync(v -> provider.createInDB(dataBaseAdapter, account.getId(), entity), dbWriteHighPriorityExecutor)
//                .thenAcceptAsync(entity::setLocalId)
//                .thenApplyAsync(v -> entity);
//    }
//
//    private <T extends IRemoteEntity> CompletableFuture<T> pushCreatedEntity(@NonNull Account account,
//                                                                             @NonNull T entity,
//                                                                             @NonNull AbstractSyncDataProvider<T> provider,
//                                                                             @Nullable BiConsumer<T, T> serverResponseInterceptor) {
//        final var serverAdapter = getServerAdapter(account);
//        final var future = new CompletableFuture<T>();
//
//        provider.createOnServer(serverAdapter, dataBaseAdapter, account.getId(), ResponseCallback.forwardTo(account, future), entity);
//
//        return future.thenApplyAsync(response -> {
//
//            response.setAccountId(account.getId());
//            response.setLocalId(entity.getLocalId());
//            response.setStatus(DBStatus.UP_TO_DATE.getId());
//
//            if (serverResponseInterceptor != null) {
//                serverResponseInterceptor.accept(entity, response);
//            }
//
//            provider.updateInDB(dataBaseAdapter, account.getId(), response, false);
//
//            return response;
//        }, dbWriteHighPriorityExecutor);
//    }
//
//    protected <T extends IRemoteEntity> CompletableFuture<T> updateEntityAndRequireSync(@NonNull Account account,
//                                                                                        @NonNull T entity,
//                                                                                        @NonNull AbstractSyncDataProvider<T> provider) {
//        return updateEntity(account, entity, provider)
//                .thenApplyAsync(updatedEntity -> {
//                    syncScheduler.pushSingleEntity(account, () -> pushUpdatedEntity(account, entity, provider));
//                    return updatedEntity;
//                });
//    }
//
//    protected <T extends IRemoteEntity> CompletableFuture<T> updateEntityAndScheduleSync(@NonNull Account account,
//                                                                                         @NonNull T entity,
//                                                                                         @NonNull AbstractSyncDataProvider<T> provider) {
//        return updateEntity(account, entity, provider)
//                .thenComposeAsync(pushedEntity -> pushUpdatedEntity(account, entity, provider));
//    }
//
//    private <T extends IRemoteEntity> CompletableFuture<T> updateEntity(@NonNull Account account,
//                                                                        @NonNull T entity,
//                                                                        @NonNull AbstractSyncDataProvider<T> provider) {
//        return runAsync(() -> entity.setStatus(DBStatus.LOCAL_EDITED.getId()))
//                .thenRunAsync(() -> provider.updateInDB(dataBaseAdapter, account.getId(), entity), dbWriteHighPriorityExecutor)
//                .thenApplyAsync(v -> entity);
//    }
//
//    private <T extends IRemoteEntity> CompletableFuture<T> pushUpdatedEntity(@NonNull Account account,
//                                                                             @NonNull T entity,
//                                                                             @NonNull AbstractSyncDataProvider<T> provider) {
//        final var serverAdapter = getServerAdapter(account);
//        final var future = new CompletableFuture<T>();
//
//        provider.updateOnServer(serverAdapter, dataBaseAdapter, account.getId(), ResponseCallback.forwardTo(account, future), entity);
//
//        return future.thenApplyAsync(response -> {
//            entity.setStatus(DBStatus.UP_TO_DATE.getId());
//            provider.updateInDB(dataBaseAdapter, account.getId(), entity, false);
//            return response;
//        }, dbWriteHighPriorityExecutor);
//    }
//
//    protected <T extends IRemoteEntity> CompletableFuture<T> deleteEntityAndRequireSync(@NonNull Account account,
//                                                                                        @NonNull T entity,
//                                                                                        @NonNull AbstractSyncDataProvider<T> provider) {
//        return deleteEntity(account, entity, provider)
//                .thenApplyAsync(deletedEntity -> {
//                    syncScheduler.pushSingleEntity(account, () -> pushDeletedEntity(account, entity, provider));
//                    return deletedEntity;
//                });
//    }
//
//    protected <T extends IRemoteEntity> CompletableFuture<T> pushEntityAndScheduleSync(@NonNull Account account,
//                                                                                       @NonNull T entity,
//                                                                                       @NonNull AbstractSyncDataProvider<T> provider) {
//        return pushDeletedEntity(account, entity, provider)
//                .thenComposeAsync(pushedEntity -> pushDeletedEntity(account, entity, provider))
//                .thenApplyAsync(emptyResponse -> entity);
//    }
//
//    protected <T extends IRemoteEntity> CompletableFuture<EmptyResponse> deleteEntityAndScheduleSync(@NonNull Account account,
//                                                                                                     @NonNull T entity,
//                                                                                                     @NonNull AbstractSyncDataProvider<T> provider) {
//        return deleteEntity(account, entity, provider)
//                .thenComposeAsync(pushedEntity -> pushDeletedEntity(account, entity, provider));
//    }
//
//    private <T extends IRemoteEntity> CompletableFuture<T> deleteEntity(@NonNull Account account,
//                                                                        @NonNull T entity,
//                                                                        @NonNull AbstractSyncDataProvider<T> provider) {
//        return runAsync(() -> entity.setStatus(DBStatus.LOCAL_DELETED.getId()))
//                .thenRunAsync(() -> {
//                    final long accountId = account.getId();
//
//                    if (entity.getId() == null) {
//
//                        // known to server?
//                        provider.deletePhysicallyInDB(dataBaseAdapter, accountId, entity);
//
//                    } else {
//
//                        // junk, bye.
//                        provider.deleteInDB(dataBaseAdapter, accountId, entity);
//
//                    }
//
//                }, dbWriteHighPriorityExecutor)
//                .thenApplyAsync(v -> entity);
//    }
//
//    private <T extends IRemoteEntity> CompletableFuture<EmptyResponse> pushDeletedEntity(@NonNull Account account,
//                                                                                         @NonNull T entity,
//                                                                                         @NonNull AbstractSyncDataProvider<T> provider) {
//        final var serverAdapter = getServerAdapter(account);
//        final var future = new CompletableFuture<EmptyResponse>();
//
//        provider.deleteOnServer(serverAdapter, account.getId(), ResponseCallback.forwardTo(account, future), entity, dataBaseAdapter);
//
//        return future.thenApplyAsync(response -> {
//            provider.deletePhysicallyInDB(dataBaseAdapter, account.getId(), entity);
//            return response;
//        }, dbWriteHighPriorityExecutor);
//    }
//
//    protected final ServerAdapter getServerAdapter(@NonNull Account account) {
//        try {
//            final var ssoAccount = AccountImporter.getSingleSignOnAccount(context, account.getName());
//            return new ServerAdapter(context, ssoAccount, connectivityUtil);
//
//        } catch (NextcloudFilesAppAccountNotFoundException e) {
//            throw new CompletionException(e);
//        }
//    }
//
//    /**
//     * Wraps a checked exception in a {@link CompletionException} and throws it
//     *
//     * @param throwable checked exception to be thrown within a
//     *                  {@link java.util.concurrent.CompletionStage}
//     */
//    protected final void throwError(@NonNull Throwable throwable) {
//        throw throwable instanceof CompletionException
//                ? (CompletionException) throwable
//                : new CompletionException(throwable);
//    }


    private static class PriorityThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return newThread(r, Thread.NORM_PRIORITY);
        }

        public Thread newThread(Runnable r, int priority) {
            final var thread = Executors.defaultThreadFactory().newThread(r);
            thread.setPriority(priority);
            return thread;
        }
    }
}
