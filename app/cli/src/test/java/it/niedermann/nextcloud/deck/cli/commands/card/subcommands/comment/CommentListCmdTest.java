package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.comment;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListCommentsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CommentListCmdTest {

    @Mock
    CardArgResolver cardArgResolver;

    @Mock
    ListCommentsUseCase listCommentsUseCase;

    @InjectMocks
    CommentListCmd commentListCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var cardId = new Card.ID(1L);
        final var comment = new Comment(
                new Comment.ID(2L),
                cardId,
                new User.ID("user"),
                OffsetDateTime.now(),
                "Test Comment",
                null,
                null,
                DBStatus.UP_TO_DATE,
                OffsetDateTime.now()
        );
        commentListCmd.cardId = 1L;

        when(listCommentsUseCase.execute(cardId)).thenReturn(Flowable.just(List.of(comment)));

        final var result = commentListCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
