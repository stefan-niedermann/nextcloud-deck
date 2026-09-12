package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.entity.AttachmentEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.AttachmentMapper;
import it.niedermann.nextcloud.deck.data.shared.AttachmentType;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.AttachmentPreview;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.model.PreviewMode;
import it.niedermann.nextcloud.deck.domain.model.query.Attachment;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import it.niedermann.nextcloud.deck.domain.state.AttachmentDownloadProgress;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Inject;
import retrofit2.HttpException;

public class AttachmentRepositoryImpl implements AttachmentRepository {

    private static final Logger logger = Logger.getLogger(AttachmentRepositoryImpl.class.getName());

    private final AttachmentDao attachmentDao;
    private final CardDao cardDao;
    private final AttachmentMapper attachmentMapper;
    private final ApiProvider.Factory apiFactory;
    private final AccountRepository accountRepository;

    @Inject
    public AttachmentRepositoryImpl(AttachmentDao attachmentDao,
                                   CardDao cardDao,
                                   AttachmentMapper attachmentMapper,
                                   ApiProvider.Factory apiFactory,
                                   AccountRepository accountRepository) {
        this.attachmentDao = attachmentDao;
        this.cardDao = cardDao;
        this.attachmentMapper = attachmentMapper;
        this.apiFactory = apiFactory;
        this.accountRepository = accountRepository;
    }

    @Override
    public Flow.Publisher<List<Attachment>> getNotDeletedAttachments(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                attachmentDao.getAttachmentsByCard(cardId.value())
                        .map(attachmentMapper::toQueryTOList)
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

    @Override
    public CompletableFuture<AttachmentPreview> getPreview(Account account, Attachment.ID attachmentId, int sizeInPx, PreviewMode mode) {
        final var ocsApi = apiFactory.create(account).getOcsApi();
        return attachmentDao.getAttachmentById(attachmentId.value())
                .thenCompose(entity -> {
                    if (entity == null || entity.getFileId() == null) {
                        final var future = new CompletableFuture<AttachmentPreview>();
                        future.completeExceptionally(new IllegalArgumentException("Attachment or fileId not found: " + attachmentId.value()));
                        return future;
                    }
                    return ocsApi.getPreview(entity.getFileId(), sizeInPx, sizeInPx, 1, mode)
                            .thenApplyAsync(response -> {
                                if (response.isSuccessful()) {
                                    try (final var body = response.body()) {
                                        if (body != null) {
                                            final var contentType = body.contentType();
                                            final var mimeType = contentType != null ? contentType.toString() : null;
                                            final var eTag = response.headers().get("ETag");
                                            final var content = body.bytes();
                                            return new AttachmentPreview(mimeType, eTag, sizeInPx, content);
                                        } else {
                                            throw new IOException("Empty response body");
                                        }
                                    } catch (IOException exception) {
                                        throw new CompletionException(exception);
                                    }
                                } else {
                                    throw new HttpException(response);
                                }
                            });
                });
    }

    @Override
    public CompletableFuture<AttachmentPreview> getPreview(Attachment.ID attachmentId, int sizeInPx, PreviewMode mode) {
        return attachmentDao.getAttachmentById(attachmentId.value())
                .thenCompose(entity -> {
                    if (entity == null) {
                        final var future = new CompletableFuture<AttachmentPreview>();
                        future.completeExceptionally(new IllegalArgumentException("Attachment not found: " + attachmentId.value()));
                        return future;
                    }
                    return accountRepository.getAccountSync(new Account.ID(entity.getAccountId()))
                            .thenCompose(account -> getPreview(account, attachmentId, sizeInPx, mode));
                });
    }
}
