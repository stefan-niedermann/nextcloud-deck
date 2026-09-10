package it.niedermann.nextcloud.deck.cli.commands.card.subcommands;

import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CardAddCmdTest {

    @Mock
    AddCardUseCase addCardUseCase;

    @InjectMocks
    CardAddCmd cardAddCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        cardAddCmd.title = "New Card";
        cardAddCmd.columnId = 2L;

        when(addCardUseCase.execute(any(CreateCard.class))).thenReturn(CompletableFuture.completedFuture(null));

        final var result = cardAddCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
