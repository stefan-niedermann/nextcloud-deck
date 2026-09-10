package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class LabelListCmdTest {

    @Mock
    BoardArgResolver boardArgResolver;

    @Mock
    ListLabelsUseCase listLabelsUseCase;

    @InjectMocks
    LabelListCmd labelListCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var boardId = new Board.ID(1L);
        final var label = new Label(new Label.ID(2L), boardId, "Label", new Color(0, 0, 0));
        labelListCmd.boardId = 1L;

        when(listLabelsUseCase.execute(boardId)).thenReturn(Maybe.just(Set.of(label)).toFlowable());

        final var result = labelListCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
