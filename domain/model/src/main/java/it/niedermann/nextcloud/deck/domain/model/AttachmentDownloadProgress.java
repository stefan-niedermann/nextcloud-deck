package it.niedermann.nextcloud.deck.domain.model;

public record AttachmentDownloadProgress(Attachment.ID attachmentId,
                                         long bytesDownloaded,
                                         long bytesTotal) {

    @Override
    public String toString() {
        return AttachmentDownloadProgress.class.getSimpleName() + " " + attachmentId + ": " + bytesDownloaded + " / " + bytesTotal;
    }
}
