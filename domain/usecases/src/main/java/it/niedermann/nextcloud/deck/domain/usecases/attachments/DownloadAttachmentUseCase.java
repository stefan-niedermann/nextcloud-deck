package it.niedermann.nextcloud.deck.domain.usecases.attachments;

import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.AttachmentDownloadProgress;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import jakarta.inject.Inject;

public class DownloadAttachmentUseCase {

    private final AttachmentRepository attachmentsRepository;

    @Inject
    public DownloadAttachmentUseCase(AttachmentRepository attachmentsRepository) {
        this.attachmentsRepository = attachmentsRepository;
    }

    public Flow.Publisher<AttachmentDownloadProgress> execute(long attachmentId) {
        return this.attachmentsRepository.download(attachmentId);
    }
}
