package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.AttachmentDownloadProgress;

public interface AttachmentRepository {

    Flow.Publisher<List<Attachment>> getNotDeletedAttachments(long cardId);

    /// @implSpec if a download for this attachmentId is already in progress, the existing [Flow.Publisher] instance must be returned
    Flow.Publisher<AttachmentDownloadProgress> download(long attachmentId);
}