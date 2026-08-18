package it.niedermann.nextcloud.deck.javafx.services.stage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jthemedetecor.OsThemeDetector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.subscribers.TestSubscriber;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.FilterInformation;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.CopyCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.DeleteCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.services.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.main.MainService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.features.PickStackFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;

@ExtendWith(ApplicationExtension.class)
class MainServiceTest {

    private StoreLogger storeLogger;
    private ThemeService themeService;
    private ApplicationRouter applicationRouter;
    private SetCurrentAccountUseCase setCurrentAccountUseCase;
    private GetCurrentBoardUseCase getCurrentBoardUseCase;
    private SetCurrentBoardUseCase setCurrentBoardUseCase;
    private DeleteCardUseCase deleteCardUseCase;
    private MoveCardUseCase moveCardUseCase;
    private CopyCardUseCase copyCardUseCase;
    private Inflater inflater;
    private PickStackFeature.Factory pickStackFeatureFactory;
    private GetBoardUseCase getBoardUseCase;

    private MainService mainService;

    @BeforeEach
    void setUp() {
        storeLogger = mock(StoreLogger.class);
        
        final var detector = mock(OsThemeDetector.class);
        when(detector.isDark()).thenReturn(false);
        final var keyValueStore = mock(KeyValueStore.class);
        when(keyValueStore.getString(ThemeService.KEY_THEME)).thenReturn(Flowable.empty());
        themeService = new ThemeService(detector, keyValueStore);

        applicationRouter = mock(ApplicationRouter.class);

        setCurrentAccountUseCase = mock(SetCurrentAccountUseCase.class);
        getCurrentBoardUseCase = mock(GetCurrentBoardUseCase.class);
        setCurrentBoardUseCase = mock(SetCurrentBoardUseCase.class);
        deleteCardUseCase = mock(DeleteCardUseCase.class);
        moveCardUseCase = mock(MoveCardUseCase.class);
        copyCardUseCase = mock(CopyCardUseCase.class);
        inflater = mock(Inflater.class);
        pickStackFeatureFactory = mock(PickStackFeature.Factory.class);
        getBoardUseCase = mock(GetBoardUseCase.class);

        mainService = new MainService(
                storeLogger,
                themeService,
                applicationRouter,
                setCurrentAccountUseCase,
                getCurrentBoardUseCase,
                setCurrentBoardUseCase,
                deleteCardUseCase,
                moveCardUseCase,
                copyCardUseCase,
                inflater,
                pickStackFeatureFactory,
                getBoardUseCase,
                new MainService.State(Optional.empty(), Optional.empty(), Optional.empty(), FilterInformation.EMPTY)
        );
    }

    @Test
    void testInitialState() {
        final var testSubscriber = new TestSubscriber<MainService.State>();
        mainService.getState().subscribe(testSubscriber);

        testSubscriber.assertValue(new MainService.State(Optional.empty(), Optional.empty(), Optional.empty(), FilterInformation.EMPTY));
    }

    @Test
    void testOnAccountSelected() {
        final var accountId = new Account.ID(1);
        when(setCurrentAccountUseCase.execute(accountId)).thenReturn(CompletableFuture.completedFuture(null));
        when(getCurrentBoardUseCase.execute(accountId)).thenReturn(CompletableFuture.completedFuture(null));

        final var testSubscriber = new TestSubscriber<MainService.State>();
        mainService.getState().subscribe(testSubscriber);

        mainService.onAccountSelected(accountId);

        testSubscriber.assertValues(
                new MainService.State(Optional.empty(), Optional.empty(), Optional.empty(), FilterInformation.EMPTY),
                new MainService.State(Optional.of(accountId), Optional.empty(), Optional.empty(), FilterInformation.EMPTY)
        );

        verify(setCurrentAccountUseCase).execute(accountId);
    }

    @Test
    void testOnBoardSelected() {
        final var accountId = new Account.ID(1);
        final var boardId = new Board.ID(2);
        
        // Prepare state with account
        mainService.dispatch(new MainService.Action.SwitchAccountAction(accountId));
        
        when(setCurrentBoardUseCase.execute(accountId, boardId)).thenReturn(CompletableFuture.completedFuture(null));

        final var testSubscriber = new TestSubscriber<MainService.State>();
        mainService.getState().subscribe(testSubscriber);

        mainService.onBoardSelected(boardId);

        testSubscriber.assertValues(
                new MainService.State(Optional.of(accountId), Optional.empty(), Optional.empty(), FilterInformation.EMPTY),
                new MainService.State(Optional.of(accountId), Optional.of(boardId), Optional.empty(), FilterInformation.EMPTY)
        );

        verify(setCurrentBoardUseCase).execute(accountId, boardId);
    }

    @Test
    void testOnEditBoard() {
        final var accountId = new Account.ID(1);
        final var board = mock(Board.class);
        final var boardId = new Board.ID(2);
        when(board.id()).thenReturn(boardId);

        mainService.dispatch(new MainService.Action.SwitchAccountAction(accountId));
        
        mainService.onEditBoard(board);

        verify(applicationRouter).launchEditBoardStage(accountId, boardId);
    }

    @Test
    void testOnOpenCardInNewWindow() {
        final var cardId = new it.niedermann.nextcloud.deck.domain.model.Card.ID(1);
        
        mainService.onOpenCardInNewWindow(cardId);
        
        verify(applicationRouter).launchEditCardStage(cardId);
    }
}
