package it.niedermann.nextcloud.deck.domain.usecases.attachments;

import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import jakarta.inject.Inject;

public class AddAttachmentUseCase {

    private final AttachmentRepository attachmentsRepository;

    @Inject
    public AddAttachmentUseCase(AttachmentRepository attachmentsRepository) {
        this.attachmentsRepository = attachmentsRepository;
    }

    public CompletableFuture<Void> execute(Card.ID cardId, Collection<Path> localPaths) {
        return CompletableFuture.allOf(localPaths.stream()
                .map(localPath -> execute(cardId, localPath))
                .toArray(CompletableFuture[]::new));
    }

    public CompletableFuture<Void> execute(Card.ID cardId, Path localPath) {
        return CompletableFuture.completedFuture(null);
    }
}
