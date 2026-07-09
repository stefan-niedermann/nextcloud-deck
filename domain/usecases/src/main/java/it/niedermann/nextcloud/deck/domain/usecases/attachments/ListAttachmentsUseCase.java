package it.niedermann.nextcloud.deck.domain.usecases.attachments;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import jakarta.inject.Inject;

public class ListAttachmentsUseCase {

    private final AttachmentRepository attachmentsRepository;

    @Inject
    public ListAttachmentsUseCase(AttachmentRepository attachmentsRepository) {
        this.attachmentsRepository = attachmentsRepository;
    }

    public Flow.Publisher<List<Attachment>> execute(Card.ID cardId) {
        return this.attachmentsRepository.getNotDeletedAttachments(cardId);
    }
}
