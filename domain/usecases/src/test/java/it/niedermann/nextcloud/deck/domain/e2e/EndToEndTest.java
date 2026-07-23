package it.niedermann.nextcloud.deck.domain.e2e;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Maybe;
import it.niedermann.nextcloud.auth.apptoken.AppTokenAuthProvider;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.di.DaggerTestComponent;
import it.niedermann.nextcloud.deck.domain.di.TestModule;
import it.niedermann.nextcloud.deck.domain.model.CreateBoard;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import jakarta.inject.Inject;

@RunWith(JUnit4.class)
public class EndToEndTest {

    private static final Logger logger = Logger.getLogger(EndToEndTest.class.getName());

    @Inject
    @TestModule.NamedUrl
    URL url;
    @Inject
    @TestModule.NamedUsername
    String username;
    @Inject
    @TestModule.NamedPassword
    String password;
    @Inject
    DeckDatabase db;
    @Inject
    KeyValueStore prefs;
    @Inject
    AppTokenAuthProvider authProvider;
    @Inject
    SyncScheduler syncScheduler;
    @Inject
    ImportAccountUseCase importAccountUseCase;
    @Inject
    GetCurrentAccountUseCase getCurrentAccountUseCase;
    @Inject
    SetCurrentAccountUseCase setCurrentAccountUseCase;
    @Inject
    AddBoardUseCase addBoardUseCase;
    @Inject
    ListBoardsUseCase listBoardsUseCase;

    @Before
    public void setup() {
        DaggerTestComponent.create().inject(this);
    }

    @After
    public void close() {
        db.close();
        prefs.clear();
    }

    @Test
    public void loginFlow() throws IOException {

        final var token = authProvider.generateToken(url, username, password);
        final var lastSyncStatus = Maybe.fromPublisher(FlowAdapters.toPublisher(importAccountUseCase.execute(url, username, token))).blockingGet();

        final var account = lastSyncStatus.account();
        setCurrentAccountUseCase.execute(account.id()).join();
        final var createBoard = new CreateBoard(account.id(), "Sample Board Title");

        final var createdBoardId = addBoardUseCase.addBoard(createBoard).join();
        final var boards = Maybe.fromPublisher(FlowAdapters.toPublisher(listBoardsUseCase.execute(account.id()))).blockingGet();
        Assert.assertTrue("Should ", boards.stream().anyMatch(board -> Objects.equals(board.id(), createdBoardId)));
//        syncScheduler.scheduleSynchronization(account.id());

    }
}
