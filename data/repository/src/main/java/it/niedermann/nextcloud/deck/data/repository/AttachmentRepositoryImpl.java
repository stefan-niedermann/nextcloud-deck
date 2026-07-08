package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.BehaviorProcessor;
import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.AttachmentDownloadProgress;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import jakarta.inject.Inject;

public class AttachmentRepositoryImpl implements AttachmentRepository {

    @Inject
    public AttachmentRepositoryImpl() {

    }

    @Override
    public Flow.Publisher<List<Attachment>> getNotDeletedAttachments(long cardId) {
        return FlowAdapters.toFlowPublisher(Flowable.just(Arrays.stream(
                        MockData.MOCK_ATTACHMENTS)
                .filter(attachment -> attachment.cardId() == cardId)
                .collect(Collectors.toList())));
    }

    @Override
    public Flow.Publisher<AttachmentDownloadProgress> download(long attachmentId) {
        final var result = BehaviorProcessor.<AttachmentDownloadProgress>create();
        System.out.println("[Mock][" + AttachmentRepositoryImpl.class.getSimpleName() + "/download]: " + attachmentId);

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