package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;

public interface SyncProvider<P> {
    /**
     * Pushes local changes of this entity type to the server.
     * @param account The account to sync.
     * @param status The current sync status.
     * @param reporter The reporter to update.
     * @return A future that completes when up-sync is done.
     */
    CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter);

    /**
     * Pulls changes of this entity type from the server.
     * @param account The account to sync.
     * @param parent The parent entity (remote DTO).
     * @param parentLocalId The local ID of the parent entity.
     * @param status The current sync status.
     * @param reporter The reporter to update.
     * @return A future that completes when down-sync is done.
     */
    CompletableFuture<Void> downSync(Account account, P parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter);
}
