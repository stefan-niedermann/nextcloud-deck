package it.niedermann.nextcloud.deck.domain.e2e.usecases.attachments;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;

public class AttachmentEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private VirtualDeviceAndAccount DEVICE_B;
    private Card cardA;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");

        final var boardTitle = randomUtil.randomize("boardForAttachment");
        final Board board = EndToEndUtil.createBoard(DEVICE_A, boardTitle);

        final var columnTitle = randomUtil.randomize("columnForAttachment");
        final Column column = EndToEndUtil.createColumn(DEVICE_A, board, columnTitle);

        final var cardTitle = randomUtil.randomize("cardForAttachment");
        cardA = EndToEndUtil.createCard(DEVICE_A, column, cardTitle);
    }

    @Test
    public void testAddAttachment() throws IOException {
        final var addAttachmentUseCase = DEVICE_A.virtualDevice().getAddAttachmentUseCase();

        final Path tempFile = Files.createTempFile("deck-e2e-attachment", ".txt");
        Files.writeString(tempFile, "Hello Deck E2E");

        addAttachmentUseCase.execute(cardA.id(), tempFile).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        final var listAttachmentsUseCaseB = DEVICE_B.virtualDevice().getListAttachmentsUseCase();
        final Collection<Attachment> attachmentsB = Flowable.fromPublisher(FlowAdapters.toPublisher(listAttachmentsUseCaseB.execute(cardA.id())))
                .blockingFirst();

        Assertions.assertNotNull(attachmentsB);
    }
}
