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
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.data.repository.MockData;
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
import it.niedermann.nextcloud.deck.domain.usecases.cards.CopyCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.DeleteCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardPreviewsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnIDsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.AddCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListPreviewCommentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.SearchLabelsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.users.SearchUserUseCase;
import it.niedermann.nextcloud.deck.javafx.exception.ExceptionUnwrapper;
import it.niedermann.nextcloud.deck.javafx.services.application.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.services.application.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditCardStageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.LoginStageContext;
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
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.PickStackFeature;
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
    private ListColumnIDsUseCase listColumnIDsUseCase;
    private GetColumnUseCase getColumnUseCase;
    private ListCardPreviewsUseCase listCardPreviewsUseCase;
    private GetCardUseCase getCardUseCase;
    private SetCurrentBoardUseCase setCurrentBoardUseCase;
    private GetCurrentAccountUseCase getCurrentAccountUseCase;
    private ApplicationRouter applicationRouter;
    private MainStageContext mainStageContext;

    private static final Account.ID ACCOUNT_ID = new Account.ID(1L);
    private static final Account ACCOUNT = new Account(ACCOUNT_ID, createUrl("https://nextcloud.example.com"), "user", "token", "Account 1", MockData.MOCK_CAPABILITIES);
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
        hasAccountsUseCase = mock(HasAccountsUseCase.class, Answers.RETURNS_MOCKS);
        boardArgResolver = mock(BoardArgResolver.class, Answers.RETURNS_MOCKS);
        setCurrentAccountUseCase = mock(SetCurrentAccountUseCase.class, Answers.RETURNS_MOCKS);
        getBoardUseCase = mock(GetBoardUseCase.class, Answers.RETURNS_MOCKS);
        listBoardsUseCase = mock(ListBoardsUseCase.class, Answers.RETURNS_MOCKS);
        listColumnIDsUseCase = mock(ListColumnIDsUseCase.class, Answers.RETURNS_MOCKS);
        getColumnUseCase = mock(GetColumnUseCase.class, Answers.RETURNS_MOCKS);
        listCardPreviewsUseCase = mock(ListCardPreviewsUseCase.class, Answers.RETURNS_MOCKS);
        getCardUseCase = mock(GetCardUseCase.class, Answers.RETURNS_MOCKS);
        setCurrentBoardUseCase = mock(SetCurrentBoardUseCase.class, Answers.RETURNS_MOCKS);
        getCurrentAccountUseCase = mock(GetCurrentAccountUseCase.class, Answers.RETURNS_MOCKS);

        
        applicationRouter = mock(ApplicationRouter.class);

        when(getColumnUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listBoardsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listColumnIDsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listCardPreviewsUseCase.execute(any())).thenReturn(Flowable.empty());

        final var getCurrentBoardUseCase = mock(GetCurrentBoardUseCase.class);
        when(getCurrentBoardUseCase.execute(any())).thenReturn(CompletableFuture.completedFuture(null));
        final var deleteCardUseCase = mock(DeleteCardUseCase.class);
        when(deleteCardUseCase.execute(any())).thenReturn(CompletableFuture.completedFuture(null));
        final var moveCardUseCase = mock(MoveCardUseCase.class);
        when(moveCardUseCase.execute(any(), any(), any(Integer.class))).thenReturn(CompletableFuture.completedFuture(null));
        final var copyCardUseCase = mock(CopyCardUseCase.class);
        when(copyCardUseCase.execute(any(), any(), any(Integer.class))).thenReturn(CompletableFuture.completedFuture(null));
        final var pickStackFeatureFactory = mock(PickStackFeature.Factory.class);

        when(hasAccountsUseCase.execute()).thenReturn(Flowable.just(true));
        final var boardParsedArgs = new BoardParsedArgs(ACCOUNT_ID, BOARD_1.id());
        when(boardArgResolver.resolve(any())).thenReturn(subscriber -> subscriber.onSubscribe(new Flow.Subscription() {
            @Override
            public void request(long n) {
                subscriber.onNext(boardParsedArgs);
                subscriber.onComplete();
            }

            @Override
            public void cancel() {
            }
        }));

        final var getAccountUseCase = mock(GetAccountUseCase.class);
        when(getAccountUseCase.execute(any())).thenReturn(Flowable.just(ACCOUNT));

        when(listBoardsUseCase.execute(ACCOUNT_ID)).thenReturn(Flowable.just(List.of(BOARD_1, BOARD_2)));
        when(getBoardUseCase.execute(BOARD_1.id())).thenReturn(Flowable.just(BOARD_1));
        when(getBoardUseCase.execute(BOARD_2.id())).thenReturn(Flowable.just(BOARD_2));
        when(listColumnIDsUseCase.execute(BOARD_1.id())).thenReturn(Flowable.just(List.of(COLUMN_1.id())));
        when(getColumnUseCase.execute(COLUMN_1.id())).thenReturn(Flowable.just(COLUMN_1));
        when(listCardPreviewsUseCase.execute(COLUMN_1.id())).thenReturn(Flowable.just(List.of(CARD_1)));
        
        when(setCurrentBoardUseCase.execute(any(), any())).thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(1)));
        when(getCurrentAccountUseCase.execute()).thenReturn(CompletableFuture.completedFuture(ACCOUNT_ID));

        final var card = new Card(CARD_1.id(), CARD_1.remoteId(), COLUMN_1.id(), OffsetDateTime.now(), 0, "Card 1", "", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), false, false, 0, 0);
        when(getCardUseCase.execute(any())).thenReturn(Flowable.empty());
        when(getCardUseCase.execute(CARD_1.id())).thenReturn(Flowable.just(card));

        final var storeLogger = new StoreLogger(new com.google.gson.Gson());
        final var detector = mock(OsThemeDetector.class);
        when(detector.isDark()).thenReturn(false);
        final var keyValueStore = mock(KeyValueStore.class);
        when(keyValueStore.getString(any())).thenReturn(Flowable.empty());
        when(keyValueStore.getBoolean(any())).thenReturn(Flowable.just(false));
        final var themeService = new ThemeService(detector, keyValueStore);

        final var inflater = Inflater.getInstance();

        final MainStageContext.Factory stageContextFactory = initialState -> {
            mainStageContext = new MainStageContext(
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
                    initialState
            );
            return mainStageContext;
        };

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
                () -> new AccountSwitcherFeature(
                        new it.niedermann.nextcloud.deck.javafx.ui.cellfactories.AccountListItemCellFactory(),
                        getAccountUseCase,
                        getAccountsUseCase,
                        mock(ScheduleSyncUseCase.class),
                        mock(RemoveAccountUseCase.class)
                ),
                viewModel
        );

        final EditCardFeature.Factory editCardFeatureFactory = viewModel -> {
            final var userSearchViewConverter = new UserSearchViewConverter();
            return new EditCardFeature(
                    new CommentCellFactory(),
                    new LabelSuggestionProvider(mock(SearchLabelsUseCase.class)),
                    new UserSuggestionProvider(mock(SearchUserUseCase.class)),
                    new LabelSearchViewConverter(),
                    new LabelTagViewFactory(new ColorUtil()),
                    userSearchViewConverter,
                    new UserTagViewFactory(userSearchViewConverter),
                    viewModel
            );
        };
        
        final var listAttachmentsUseCase = mock(ListAttachmentsUseCase.class, Answers.RETURNS_MOCKS);
        when(listAttachmentsUseCase.execute(any())).thenReturn(Flowable.just(Collections.emptyList()));
        final var listPreviewCommentsUseCase = mock(ListPreviewCommentsUseCase.class, Answers.RETURNS_MOCKS);
        when(listPreviewCommentsUseCase.execute(any())).thenReturn(Flowable.just(Collections.emptyList()));
        final var listPreviewActivitiesUseCase = mock(ListPreviewActivitiesUseCase.class, Answers.RETURNS_MOCKS);
        when(listPreviewActivitiesUseCase.execute(any())).thenReturn(Flowable.just(Collections.emptyList()));

        final EditCardStageContext.Factory editCardStageContextFactory = (initialState, onClose) -> new EditCardStageContext(
                storeLogger,
                applicationRouter,
                getCardUseCase,
                mock(UpdateCardUseCase.class, Answers.RETURNS_MOCKS),
                getColumnUseCase,
                getBoardUseCase,
                listAttachmentsUseCase,
                listPreviewCommentsUseCase,
                listPreviewActivitiesUseCase,
                mock(AddCommentUseCase.class, Answers.RETURNS_MOCKS),
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
        final LoginStageContext.Factory loginStageContextFactory = url -> new LoginStageContext(
                storeLogger,
                mock(it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase.class),
                url
        );
        final var exceptionUnwrapper = new ExceptionUnwrapper();

        final var manager = new MainStageManager(
                inflater,
                stage,
                themeService,
                splashScreenFactory,
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                mainSceneFactory,
                stageContextFactory,
                exceptionUnwrapper,
                boardArgResolver,
                new BoardRawArgs.CurrentBoardOfCurrentAccount()
        );
        manager.initialize();
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
                listColumnIDsUseCase,
                viewModel
        );
    }

    @Test
    void testMainSceneIsShown(FxRobot robot) throws TimeoutException {
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#boardList").tryQuery().isPresent());
        FxAssert.verifyThat("#boardList", NodeMatchers.isVisible());
    }

    @Test
    void testSwitchBetweenBoards(FxRobot robot) throws TimeoutException {
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("Board 2").tryQuery().isPresent());
        robot.clickOn("Board 2");
        verify(setCurrentBoardUseCase, atLeastOnce()).execute(ACCOUNT_ID, BOARD_2.id());
    }

    @Test
    void testOpenCard(FxRobot robot) throws TimeoutException {
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("Board 1").tryQuery().isPresent());
        robot.clickOn("Board 1");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("Card 1").tryQuery().isPresent());
        robot.clickOn("Card 1");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#popOutBtn").tryQuery().isPresent());
        FxAssert.verifyThat("#popOutBtn", NodeMatchers.isVisible());
    }

    @Test
    void testCloseCard(FxRobot robot) throws TimeoutException {
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("Board 1").tryQuery().isPresent());
        robot.clickOn("Board 1");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("Card 1").tryQuery().isPresent());
        robot.clickOn("Card 1");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#popOutBtn").tryQuery().isPresent());
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
    void testPopOutCard(FxRobot robot) throws TimeoutException {
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("Board 1").tryQuery().isPresent());
        robot.clickOn("Board 1");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("Card 1").tryQuery().isPresent());
        robot.clickOn("Card 1");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#popOutBtn").tryQuery().isPresent());
        robot.clickOn("#popOutBtn");
        verify(applicationRouter, atLeastOnce()).launchEditCardStage(CARD_1.id());
    }
}
