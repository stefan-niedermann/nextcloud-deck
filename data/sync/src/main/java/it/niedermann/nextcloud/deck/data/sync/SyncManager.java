package it.niedermann.nextcloud.deck.data.sync;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.sync.provider.AccessControlSyncProvider;
import it.niedermann.nextcloud.deck.data.sync.provider.AttachmentSyncProvider;
import it.niedermann.nextcloud.deck.data.sync.provider.BoardSyncProvider;
import it.niedermann.nextcloud.deck.data.sync.provider.CardSyncProvider;
import it.niedermann.nextcloud.deck.data.sync.provider.ColumnSyncProvider;
import it.niedermann.nextcloud.deck.data.sync.provider.CommentSyncProvider;
import it.niedermann.nextcloud.deck.data.sync.provider.LabelSyncProvider;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class SyncManager {

    private static final Logger logger = Logger.getLogger(SyncManager.class.getName());

    private final ApiProvider.Factory apiProviderFactory;
    private final BoardSyncProvider boardSyncProvider;
    private final ColumnSyncProvider columnSyncProvider;
    private final CardSyncProvider cardSyncProvider;
    private final LabelSyncProvider labelSyncProvider;
    private final AttachmentSyncProvider attachmentSyncProvider;
    private final CommentSyncProvider commentSyncProvider;
    private final AccessControlSyncProvider accessControlSyncProvider;

    @Inject
    public SyncManager(
            ApiProvider.Factory apiProviderFactory,
            BoardSyncProvider boardSyncProvider,
            ColumnSyncProvider columnSyncProvider,
            CardSyncProvider cardSyncProvider,
            LabelSyncProvider labelSyncProvider,
            AttachmentSyncProvider attachmentSyncProvider,
            CommentSyncProvider commentSyncProvider,
            AccessControlSyncProvider accessControlSyncProvider
    ) {
        this.apiProviderFactory = apiProviderFactory;
        this.boardSyncProvider = boardSyncProvider;
        this.columnSyncProvider = columnSyncProvider;
        this.cardSyncProvider = cardSyncProvider;
        this.labelSyncProvider = labelSyncProvider;
        this.attachmentSyncProvider = attachmentSyncProvider;
        this.commentSyncProvider = commentSyncProvider;
        this.accessControlSyncProvider = accessControlSyncProvider;
        
        this.boardSyncProvider.setColumnSyncProvider(columnSyncProvider);
        this.boardSyncProvider.setLabelSyncProvider(labelSyncProvider);
        this.boardSyncProvider.setAccessControlSyncProvider(accessControlSyncProvider);
        this.columnSyncProvider.setCardSyncProvider(cardSyncProvider);
        this.cardSyncProvider.setAttachmentSyncProvider(attachmentSyncProvider);
        this.cardSyncProvider.setCommentSyncProvider(commentSyncProvider);
    }

    public CompletableFuture<Void> synchronize(Account account, Consumer<SyncStatus> reporter) {
        final var apiProvider = apiProviderFactory.create(account);
        final var ocsApi = apiProvider.getOcsApi();

        SyncStatus initialStatus = new SyncStatus(account);

        logger.info("Starting sync for account: " + account.username());
        return ocsApi.getCapabilities(null).thenCompose(response -> {
            logger.info("Server capabilities received");

            reporter.accept(initialStatus);
            return boardSyncProvider.upSync(account, initialStatus, reporter)
                    .thenCompose(v -> {
                        logger.info("Board up-sync finished");
                        return columnSyncProvider.upSync(account, initialStatus, reporter);
                    })
                    .thenCompose(v -> {
                        logger.info("Column up-sync finished");
                        return cardSyncProvider.upSync(account, initialStatus, reporter);
                    })
                    .thenCompose(v -> {
                        logger.info("Card up-sync finished");
                        return labelSyncProvider.upSync(account, initialStatus, reporter);
                    })
                    .thenCompose(v -> {
                        logger.info("Label up-sync finished");
                        return attachmentSyncProvider.upSync(account, initialStatus, reporter);
                    })
                    .thenCompose(v -> {
                        logger.info("Attachment up-sync finished");
                        return commentSyncProvider.upSync(account, initialStatus, reporter);
                    })
                    .thenCompose(v -> {
                        logger.info("Comment up-sync finished");
                        return accessControlSyncProvider.upSync(account, initialStatus, reporter);
                    });
        }).thenCompose(v -> {
            logger.info("Starting down-sync");
            return boardSyncProvider.downSync(account, null, null, initialStatus, reporter);
        }).thenAccept(v -> {
            logger.info("Sync finished for account: " + account.username());
        }).exceptionally(throwable -> {
            logger.log(Level.SEVERE, "Sync failed", throwable);
            throw new RuntimeException(throwable);
        });
    }
}
