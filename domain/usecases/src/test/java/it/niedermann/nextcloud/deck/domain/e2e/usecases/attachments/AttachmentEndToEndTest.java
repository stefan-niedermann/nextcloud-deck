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
import it.niedermann.nextcloud.deck.domain.repository.MockData;

public class AttachmentEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private VirtualDeviceAndAccount DEVICE_B;
    private Card cardA;
    private String boardTitle;
    private String columnTitle;
    private String cardTitle;

    @BeforeEach
    public void setup() throws IOException {
        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");

        boardTitle = randomUtil.randomize("boardForAttachment");
        final Board board = EndToEndUtil.createBoard(DEVICE_A, boardTitle);

        columnTitle = randomUtil.randomize("columnForAttachment");
        final Column column = EndToEndUtil.createColumn(DEVICE_A, board, columnTitle);

        cardTitle = randomUtil.randomize("cardForAttachment");
        cardA = EndToEndUtil.createCard(DEVICE_A, column, cardTitle);
    }

    @Test
    public void testAddAttachment() throws IOException {
        final var addAttachmentUseCase = DEVICE_A.virtualDevice().getAddAttachmentUseCase();

        final String fileName = MockData.MOCK_ATTACHMENTS[0].title();
        final Path tempFile = Files.createTempFile("deck-e2e-" + fileName, ".txt");
        Files.writeString(tempFile, "Hello Deck E2E - Mock Content for " + fileName);

        addAttachmentUseCase.execute(cardA.id(), tempFile).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        // Fetch everything on Device B because local IDs might differ
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardTitle);
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnTitle);
        final var cardB = EndToEndUtil.getCard(DEVICE_B, columnB, cardTitle);

        final var listAttachmentsUseCaseB = DEVICE_B.virtualDevice().getListAttachmentsUseCase();
        final Collection<Attachment> attachmentsB = Flowable.fromPublisher(FlowAdapters.toPublisher(listAttachmentsUseCaseB.execute(cardB.id())))
                .blockingFirst();

        Assertions.assertNotNull(attachmentsB);
        Assertions.assertFalse(attachmentsB.isEmpty());
    }
}
