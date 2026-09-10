package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.attachment;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.query.Attachment;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class AttachmentListCmdTest {

    @Mock
    CardArgResolver cardArgResolver;

    @Mock
    ListAttachmentsUseCase listAttachmentsUseCase;

    @InjectMocks
    AttachmentListCmd attachmentListCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var cardId = new Card.ID(1L);
        final var attachment = new Attachment(
                new Attachment.ID(2L),
                "Test",
                OffsetDateTime.now(),
                null,
                new Attachment.FileSize(100L),
                "text/plain"
        );
        attachmentListCmd.cardId = 1L;

        when(listAttachmentsUseCase.execute(cardId)).thenReturn(Flowable.just(List.of(attachment)));

        final var result = attachmentListCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
