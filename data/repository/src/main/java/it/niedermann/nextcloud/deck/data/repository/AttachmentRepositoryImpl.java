package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.entity.AttachmentEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.AttachmentMapper;
import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.AttachmentType;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import it.niedermann.nextcloud.deck.domain.state.AttachmentDownloadProgress;
import jakarta.inject.Inject;

public class AttachmentRepositoryImpl implements AttachmentRepository {

    private static final Logger logger = Logger.getLogger(AttachmentRepositoryImpl.class.getName());

    private final AttachmentDao attachmentDao;
    private final CardDao cardDao;
    private final AttachmentMapper attachmentMapper;

    @Inject
    public AttachmentRepositoryImpl(AttachmentDao attachmentDao,
                                   CardDao cardDao,
                                   AttachmentMapper attachmentMapper) {
        this.attachmentDao = attachmentDao;
        this.cardDao = cardDao;
        this.attachmentMapper = attachmentMapper;
    }

    @Override
    public Flow.Publisher<List<Attachment>> getNotDeletedAttachments(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                attachmentDao.getAttachmentsByCard(cardId.value())
                        .map(attachmentMapper::toTOList)
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<AttachmentDownloadProgress> download(Attachment.ID attachmentId) {
        final var result = BehaviorProcessor.<AttachmentDownloadProgress>create();
        logger.info("[Mock][download]: " + attachmentId);

        new Thread(() -> {

            final int MOCK_DURATION_PER_CHUNK = 200;
            final int MOCK_FILE_SIZE = 10;

            try {
                Thread.sleep(MOCK_DURATION_PER_CHUNK);
                for (int i = 0; i <= MOCK_FILE_SIZE; i++) {
                    Thread.sleep(MOCK_DURATION_PER_CHUNK);
                    result.onNext(new AttachmentDownloadProgress(attachmentId, i, MOCK_FILE_SIZE));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        return FlowAdapters.toFlowPublisher(result);
    }

    @Override
    public CompletableFuture<Void> addAttachment(Card.ID cardId, Path localPath) {
        return cardDao.getCardById(cardId.value())
                .thenCompose(card -> {
                    if (card == null) {
                        final var future = new CompletableFuture<Void>();
                        future.completeExceptionally(new IllegalArgumentException("Card not found: " + cardId.value()));
                        return future;
                    }
                    final var entity = new AttachmentEntity(
                            0,
                            card.getAccountId(),
                            null,
                            DBStatus.LOCAL_EDITED.getId(),
                            null,
                            OffsetDateTime.now(),
                            null,
                            cardId.value(),
                            AttachmentType.FILE,
                            localPath.getFileName().toString(),
                            OffsetDateTime.now(),
                            null, // createdBy
                            null, // deletedAt
                            0L, // filesize
                            null, // mimetype
                            null, // dirname
                            localPath.getFileName().toString(),
                            null, // extension
                            localPath.getFileName().toString(),
                            localPath.toAbsolutePath().toString(),
                            null, // fileId
                            null
                    );
                    return attachmentDao.insertOrReplace(entity).thenApply(id -> null);
                });
    }
}
