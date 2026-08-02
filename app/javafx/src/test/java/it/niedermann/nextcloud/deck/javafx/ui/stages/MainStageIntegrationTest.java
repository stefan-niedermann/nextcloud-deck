package it.niedermann.nextcloud.deck.javafx.ui.stages;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;

import com.jthemedetecor.OsThemeDetector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListPreviewActivitiesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.DeleteCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardPreviewsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.AddCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListPreviewCommentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.javafx.exception.ExceptionUnwrapper;
import it.niedermann.nextcloud.deck.javafx.services.application.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.services.application.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditCardStageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.MainStageContext;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.CardPreviewCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.CommentCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.AccountSwitcherFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.BoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.BoardListFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.ColumnFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.HeaderFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.LabelSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.UserSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders.LabelSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders.UserSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories.LabelTagViewFactory;
import it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories.UserTagViewFactory;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class MainStageIntegrationTest {

    private HasAccountsUseCase hasAccountsUseCase;
    private BoardArgResolver boardArgResolver;
    private SetCurrentAccountUseCase setCurrentAccountUseCase;
    private GetBoardUseCase getBoardUseCase;
    private ListBoardsUseCase listBoardsUseCase;
    private ListColumnsUseCase listColumnsUseCase;
    private GetColumnUseCase getColumnUseCase;
    private ListCardPreviewsUseCase listCardPreviewsUseCase;
    private GetCardUseCase getCardUseCase;
    private SetCurrentBoardUseCase setCurrentBoardUseCase;
    private GetCurrentAccountUseCase getCurrentAccountUseCase;
    private ApplicationRouter applicationRouter;
    private MainStageContext mainStageContext;

    private static final Account.ID ACCOUNT_ID = new Account.ID(1L);
    private static final Account ACCOUNT = new Account(ACCOUNT_ID, createUrl("https://nextcloud.example.com"), "user", "token", "Account 1");
    private static final Board BOARD_1 = new Board(new Board.ID(1L), "Board 1", new Color(0, 0, 255), new Board.Permissions(true, true, true, true));
    private static final Board BOARD_2 = new Board(new Board.ID(2L), "Board 2", new Color(0, 255, 0), new Board.Permissions(true, true, true, true));
    private static final Column COLUMN_1 = new Column(new Column.ID(1L), BOARD_1.id(), "Column 1", 0);
    private static final PreviewCard CARD_1 = new PreviewCard(new Card.ID(1L), new Card.RemoteID(1L), "Card 1", "", Collections.emptySet(), 0, 0, 0, false, 0, 0, null, new Color(255, 0, 0));

    private static URL createUrl(String url) {
        try {
            return URI.create(url).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Start
    void start(Stage stage) {
        hasAccountsUseCase = mock(HasAccountsUseCase.class);
        boardArgResolver = mock(BoardArgResolver.class);
        setCurrentAccountUseCase = mock(SetCurrentAccountUseCase.class);
        getBoardUseCase = mock(GetBoardUseCase.class);
        listBoardsUseCase = mock(ListBoardsUseCase.class);
        listColumnsUseCase = mock(ListColumnsUseCase.class);
        getColumnUseCase = mock(GetColumnUseCase.class);
        listCardPreviewsUseCase = mock(ListCardPreviewsUseCase.class);
        getCardUseCase = mock(GetCardUseCase.class);
        setCurrentBoardUseCase = mock(SetCurrentBoardUseCase.class);
        getCurrentAccountUseCase = mock(GetCurrentAccountUseCase.class);
        
        applicationRouter = mock(ApplicationRouter.class);

        when(getColumnUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listBoardsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listColumnsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listCardPreviewsUseCase.execute(any())).thenReturn(Flowable.empty());

        final var getCurrentBoardUseCase = mock(GetCurrentBoardUseCase.class);
        when(getCurrentBoardUseCase.execute(any())).thenReturn(CompletableFuture.completedFuture(null));
        final var deleteCardUseCase = mock(DeleteCardUseCase.class);
        when(deleteCardUseCase.execute(any())).thenReturn(CompletableFuture.completedFuture(null));

        when(hasAccountsUseCase.execute()).thenReturn(Flowable.just(true));
        final var boardParsedArgs = new BoardParsedArgs(ACCOUNT_ID, BOARD_1.id());
        when(boardArgResolver.resolve(any())).thenReturn(CompletableFuture.completedFuture(boardParsedArgs));

        final var getAccountUseCase = mock(GetAccountUseCase.class);
        when(getAccountUseCase.execute(any())).thenReturn(Flowable.just(ACCOUNT));

        when(listBoardsUseCase.execute(ACCOUNT_ID)).thenReturn(Flowable.just(List.of(BOARD_1, BOARD_2)));
        when(getBoardUseCase.execute(BOARD_1.id())).thenReturn(Flowable.just(BOARD_1));
        when(getBoardUseCase.execute(BOARD_2.id())).thenReturn(Flowable.just(BOARD_2));
        when(listColumnsUseCase.execute(BOARD_1.id())).thenReturn(Flowable.just(List.of(COLUMN_1.id())));
        when(getColumnUseCase.execute(COLUMN_1.id())).thenReturn(Flowable.just(COLUMN_1));
        when(listCardPreviewsUseCase.execute(COLUMN_1.id())).thenReturn(Flowable.just(List.of(CARD_1)));
        
        when(setCurrentBoardUseCase.execute(any(), any())).thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(1)));
        when(getCurrentAccountUseCase.execute()).thenReturn(CompletableFuture.completedFuture(ACCOUNT_ID));

        final var card = new Card(CARD_1.id(), CARD_1.remoteId(), COLUMN_1.id(), OffsetDateTime.now(), 0, "Card 1", "", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), false, false, 0, 0);
        when(getCardUseCase.execute(any())).thenReturn(Flowable.empty());
        when(getCardUseCase.execute(CARD_1.id())).thenReturn(Flowable.just(card));

        final var storeLogger = mock(StoreLogger.class);
        final var detector = mock(OsThemeDetector.class);
        when(detector.isDark()).thenReturn(false);
        final var keyValueStore = mock(KeyValueStore.class);
        when(keyValueStore.getString(any())).thenReturn(Flowable.empty());
        when(keyValueStore.getBoolean(any())).thenReturn(Flowable.just(false));
        final var themeService = new ThemeService(detector, keyValueStore);

        final MainStageContext.Factory stageContextFactory = initialState -> {
            mainStageContext = new MainStageContext(
                    storeLogger,
                    themeService,
                    applicationRouter,
                    setCurrentAccountUseCase,
                    getCurrentBoardUseCase,
                    setCurrentBoardUseCase,
                    deleteCardUseCase,
                    getBoardUseCase,
                    initialState
            );
            return mainStageContext;
        };

        final var inflater = Inflater.getInstance();

        final var getAccountsUseCase = mock(GetAccountsUseCase.class);
        when(getAccountsUseCase.execute()).thenReturn(Flowable.just(List.of(ACCOUNT)));

        final var stageTitleResolver = new StageTitleResolver(
                getAccountUseCase,
                getAccountsUseCase,
                getBoardUseCase,
                getCardUseCase,
                getColumnUseCase
        );

        final BoardFeature.Factory boardFeatureFactory = getFactory(getAccountUseCase, keyValueStore, inflater);

        final BoardListFeature.Factory boardListFeatureFactory = viewModel -> new BoardListFeature(
                getBoardUseCase,
                listBoardsUseCase,
                viewModel
        );

        final HeaderFeature.Factory headerFeatureFactory = viewModel -> new HeaderFeature(
                inflater,
                getAccountUseCase,
                mock(ScheduleSyncUseCase.class),
                mock(RemoveAccountUseCase.class),
                mock(AccountSwitcherFeature.Factory.class, Answers.RETURNS_MOCKS),
                viewModel
        );

        final EditCardFeature.Factory editCardFeatureFactory = viewModel -> new EditCardFeature(
                mock(CommentCellFactory.class, Answers.RETURNS_MOCKS),
                mock(LabelSuggestionProvider.class, Answers.RETURNS_MOCKS),
                mock(UserSuggestionProvider.class, Answers.RETURNS_MOCKS),
                mock(LabelSearchViewConverter.class, Answers.RETURNS_MOCKS),
                mock(LabelTagViewFactory.class, Answers.RETURNS_MOCKS),
                mock(UserSearchViewConverter.class, Answers.RETURNS_MOCKS),
                mock(UserTagViewFactory.class, Answers.RETURNS_MOCKS),
                viewModel
        );
        
        final var listAttachmentsUseCase = mock(ListAttachmentsUseCase.class);
        when(listAttachmentsUseCase.execute(any())).thenReturn(Flowable.just(Collections.emptyList()));
        final var listPreviewCommentsUseCase = mock(ListPreviewCommentsUseCase.class);
        when(listPreviewCommentsUseCase.execute(any())).thenReturn(Flowable.just(Collections.emptyList()));
        final var listPreviewActivitiesUseCase = mock(ListPreviewActivitiesUseCase.class);
        when(listPreviewActivitiesUseCase.execute(any())).thenReturn(Flowable.just(Collections.emptyList()));

        final EditCardStageContext.Factory editCardStageContextFactory = (initialState, onClose) -> new EditCardStageContext(
                storeLogger,
                applicationRouter,
                getCardUseCase,
                mock(UpdateCardUseCase.class),
                getColumnUseCase,
                getBoardUseCase,
                listAttachmentsUseCase,
                listPreviewCommentsUseCase,
                listPreviewActivitiesUseCase,
                mock(AddCommentUseCase.class),
                initialState,
                onClose
        );

        final MainScene.Factory mainSceneFactory = context -> new MainScene(
                inflater,
                boardListFeatureFactory,
                headerFeatureFactory,
                boardFeatureFactory,
                editCardFeatureFactory,
                editCardStageContextFactory,
                stageTitleResolver,
                context
        );

        final SplashScreenScene.Factory splashScreenFactory = SplashScreenScene::new;
        final var loginFactoryProvider = (jakarta.inject.Provider<LoginScene.Factory>) () -> mock(LoginScene.Factory.class);
        final var exceptionFactoryProvider = (jakarta.inject.Provider<ExceptionScene.Factory>) () -> mock(ExceptionScene.Factory.class);
        final var exceptionUnwrapper = new ExceptionUnwrapper();

        new MainStageManager(
                inflater,
                stage,
                themeService,
                splashScreenFactory,
                hasAccountsUseCase,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                mainSceneFactory,
                stageContextFactory,
                exceptionUnwrapper,
                boardArgResolver,
                new BoardRawArgs.CurrentBoardOfCurrentAccount()
        );
    }

    @NonNull
    private BoardFeature.Factory getFactory(GetAccountUseCase getAccountUseCase, KeyValueStore keyValueStore, Inflater inflater) {
        final var cardPreviewCellFactory = new CardPreviewCellFactory(
                getCurrentAccountUseCase,
                getAccountUseCase,
                keyValueStore,
                new ColorUtil()
        );

        final ColumnFeature.Factory columnFeatureFactory = (columnId, viewModel) -> new ColumnFeature(
                listCardPreviewsUseCase,
                mock(MoveCardUseCase.class),
                cardPreviewCellFactory,
                mock(AddCardUseCase.class),
                getColumnUseCase,
                columnId,
                viewModel
        );

        return viewModel -> new BoardFeature(
                inflater,
                getBoardUseCase,
                columnFeatureFactory,
                listColumnsUseCase,
                viewModel
        );
    }

    @Test
    void testMainSceneIsShown(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#boardList", NodeMatchers.isVisible());
    }

    @Test
    void testSwitchBetweenBoards(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Board 2");
        verify(setCurrentBoardUseCase, atLeastOnce()).execute(ACCOUNT_ID, BOARD_2.id());
    }

    @Test
    void testOpenCard(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Board 1");
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Card 1");
        WaitForAsyncUtils.waitForFxEvents();
        robot.sleep(200);
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#popOutBtn", NodeMatchers.isVisible());
    }

    @Test
    void testCloseCard(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Board 1");
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Card 1");
        WaitForAsyncUtils.waitForFxEvents();
        robot.sleep(200);
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#popOutBtn", NodeMatchers.isVisible());
        robot.clickOn("#closeSidebar");
        WaitForAsyncUtils.waitForFxEvents();
        robot.sleep(200);
        WaitForAsyncUtils.waitForFxEvents();
        if (!robot.lookup("#popOutBtn").queryAll().isEmpty()) {
            FxAssert.verifyThat("#popOutBtn", NodeMatchers.isInvisible());
        }
    }

    @Test
    void testPopOutCard(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Board 1");
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Card 1");
        WaitForAsyncUtils.waitForFxEvents();
        robot.sleep(200);
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("#popOutBtn");
        verify(applicationRouter, atLeastOnce()).launchEditCardStage(CARD_1.id());
    }
}
