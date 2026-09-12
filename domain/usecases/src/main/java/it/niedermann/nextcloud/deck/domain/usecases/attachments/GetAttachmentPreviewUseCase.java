package it.niedermann.nextcloud.deck.domain.usecases.attachments;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.AttachmentPreview;
import it.niedermann.nextcloud.deck.domain.model.PreviewMode;
import it.niedermann.nextcloud.deck.domain.model.query.Attachment;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import jakarta.inject.Inject;

public class GetAttachmentPreviewUseCase {

    private final AttachmentRepository attachmentRepository;
    private final Map<CacheKey, CompletableFuture<AttachmentPreview>> cache = new ConcurrentHashMap<>();

    @Inject
    public GetAttachmentPreviewUseCase(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public CompletableFuture<AttachmentPreview> execute(Account account, Attachment.ID attachmentId, int sizeInPx, PreviewMode mode) {
        if (account == null || attachmentId == null || mode == null) {
            final var result = new CompletableFuture<AttachmentPreview>();
            result.completeExceptionally(new IllegalArgumentException("Account, Attachment.ID and PreviewMode must not be null"));
            return result;
        }
        final var key = new CacheKey(attachmentId, sizeInPx, mode);
        return cache.computeIfAbsent(key, k -> attachmentRepository.getPreview(account, attachmentId, sizeInPx, mode));
    }

    public CompletableFuture<AttachmentPreview> execute(Attachment.ID attachmentId, int sizeInPx, PreviewMode mode) {
        if (attachmentId == null || mode == null) {
            final var result = new CompletableFuture<AttachmentPreview>();
            result.completeExceptionally(new IllegalArgumentException("Attachment.ID and PreviewMode must not be null"));
            return result;
        }
        final var key = new CacheKey(attachmentId, sizeInPx, mode);
        return cache.computeIfAbsent(key, k -> attachmentRepository.getPreview(attachmentId, sizeInPx, mode));
    }

    private record CacheKey(Attachment.ID attachmentId, int sizeInPx, PreviewMode mode) {
    }
}
