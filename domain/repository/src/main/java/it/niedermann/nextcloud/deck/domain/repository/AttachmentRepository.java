package it.niedermann.nextcloud.deck.domain.repository;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.AttachmentPreview;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.PreviewMode;
import it.niedermann.nextcloud.deck.domain.model.query.Attachment;
import it.niedermann.nextcloud.deck.domain.state.AttachmentDownloadProgress;

public interface AttachmentRepository {

    Flow.Publisher<List<Attachment>> getNotDeletedAttachments(Card.ID cardId);

    /// @implSpec if a download for this attachmentId is already in progress, the existing [Flow.Publisher] instance must be returned
    Flow.Publisher<AttachmentDownloadProgress> download(Attachment.ID attachmentId);

    CompletableFuture<Void> addAttachment(Card.ID cardId, Path localPath);

    CompletableFuture<AttachmentPreview> getPreview(Account account, Attachment.ID attachmentId, int sizeInPx, PreviewMode mode);

    CompletableFuture<AttachmentPreview> getPreview(Attachment.ID attachmentId, int sizeInPx, PreviewMode mode);
}
