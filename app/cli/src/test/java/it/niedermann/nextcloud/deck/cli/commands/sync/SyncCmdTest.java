package it.niedermann.nextcloud.deck.cli.commands.sync;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Capabilities;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SyncCmdTest {

    @Mock
    GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Mock
    ScheduleSyncUseCase scheduleSyncUseCase;

    @InjectMocks
    SyncCmd syncCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() throws Exception {
        final var accountId = new Account.ID(1L);
        final var account = new Account(accountId, URI.create("https://example.com").toURL(), "user", "token", "user@host", new Capabilities(null, null, false, false));

        when(getCurrentAccountUseCase.execute()).thenReturn(CompletableFuture.completedFuture(accountId));
        when(scheduleSyncUseCase.execute(accountId)).thenReturn(Flowable.just(new SyncStatus(account)));

        final var result = syncCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
