package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.data.local.mapper.AttachmentMapper;
import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import it.niedermann.nextcloud.deck.domain.state.AttachmentDownloadProgress;
import jakarta.inject.Inject;

public class AttachmentRepositoryImpl implements AttachmentRepository {

    private static final Logger logger = Logger.getLogger(AttachmentRepositoryImpl.class.getName());

    private final AttachmentDao attachmentDao;
    private final AttachmentMapper attachmentMapper;

    @Inject
    public AttachmentRepositoryImpl(AttachmentDao attachmentDao,
                                   AttachmentMapper attachmentMapper) {
        this.attachmentDao = attachmentDao;
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
}