package it.niedermann.nextcloud.deck.javafx.ui.stages;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jthemedetecor.OsThemeDetector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Flow;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardSharesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.RemoveBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.UpdateBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.AddColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.DeleteColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnIDsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.UpdateColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.AddLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.DeleteLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.UpdateLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.ScreenshotUtil;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.EditBoardScene;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.EditBoardService;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.EditBoardStage;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.features.EditBoardColumnsFeature;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.features.EditBoardDetailsFeature;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.features.EditBoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.features.EditBoardLabelsFeature;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.features.EditBoardShareFeature;
import it.niedermann.nextcloud.deck.javafx.ui.exception.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.searchviewconverter.UserSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders.UserSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.splashscreen.SplashScreenScene;
import javafx.application.HostServices;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class EditBoardStageIntegrationTest {

    @Start
    void start(Stage stage) {
        final var hasAccountsUseCase = mock(HasAccountsUseCase.class);
        final var boardArgResolver = mock(BoardArgResolver.class);
        final var setCurrentAccountUseCase = mock(SetCurrentAccountUseCase.class);

        when(hasAccountsUseCase.execute()).thenReturn(Flowable.just(true));

        final var accountId = new Account.ID(1);
        final var boardId = new Board.ID(2);
        final var boardParsedArgs = new BoardParsedArgs(accountId, boardId);
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

        final var storeLogger = new StoreLogger(new com.google.gson.Gson());
        final var detector = mock(OsThemeDetector.class);
        when(detector.isDark()).thenReturn(false);
        final var keyValueStore = mock(KeyValueStore.class);
        when(keyValueStore.getString(ThemeService.KEY_THEME)).thenReturn(Flowable.empty());
        final var themeService = new ThemeService(detector, keyValueStore);


        final var getBoardUseCase = mock(GetBoardUseCase.class, Answers.RETURNS_MOCKS);
        final var listCardsUseCase = mock(ListCardsUseCase.class, Answers.RETURNS_MOCKS);
        final var listColumnsUseCase = mock(ListColumnIDsUseCase.class, Answers.RETURNS_MOCKS);
        final var getColumnUseCase = mock(GetColumnUseCase.class, Answers.RETURNS_MOCKS);
        final var listLabelsUseCase = mock(ListLabelsUseCase.class, Answers.RETURNS_MOCKS);
        final var listBoardSharesUseCase = mock(ListBoardSharesUseCase.class, Answers.RETURNS_MOCKS);

        when(getBoardUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listCardsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listColumnsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(getColumnUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listLabelsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listBoardSharesUseCase.execute(any())).thenReturn(Flowable.empty());


        final var stageContext = new EditBoardService(
                storeLogger,
                getBoardUseCase,
                listCardsUseCase,
                mock(AddColumnUseCase.class),
                mock(UpdateColumnUseCase.class),
                mock(DeleteColumnUseCase.class),
                listColumnsUseCase,
                getColumnUseCase,
                mock(AddLabelUseCase.class),
                mock(UpdateLabelUseCase.class),
                mock(DeleteLabelUseCase.class),
                listLabelsUseCase,
                listBoardSharesUseCase,
                mock(AddBoardShareUseCase.class),
                mock(RemoveBoardShareUseCase.class),
                mock(UpdateBoardShareUseCase.class),
                new EditBoardService.State(accountId, boardId)
        );
        final EditBoardService.Factory stageContextFactory = initialState -> stageContext;

        final var inflater = Inflater.getInstance();

        final var getAccountUseCase = mock(GetAccountUseCase.class, Answers.RETURNS_MOCKS);
        when(getAccountUseCase.execute(any())).thenReturn(Flowable.empty());
        final var getAccountsUseCase = mock(GetAccountsUseCase.class, Answers.RETURNS_MOCKS);
        when(getAccountsUseCase.execute()).thenReturn(Flowable.empty());
        final var getCardUseCase = mock(GetCardUseCase.class, Answers.RETURNS_MOCKS);
        when(getCardUseCase.execute(any())).thenReturn(Flowable.empty());


        final var stageTitleResolver = new StageTitleResolver(
                getAccountUseCase,
                getAccountsUseCase,
                getBoardUseCase,
                getCardUseCase,
                getColumnUseCase
        );

        final var userSearchViewConverter = new UserSearchViewConverter();
        final EditBoardFeature.Factory editBoardFeatureFactory = viewModel -> new EditBoardFeature(
                inflater,
                EditBoardDetailsFeature::new,
                EditBoardColumnsFeature::new,
                EditBoardLabelsFeature::new,
                vm -> new EditBoardShareFeature(
                        new UserSuggestionProvider(mock(it.niedermann.nextcloud.deck.domain.usecases.users.SearchUserUseCase.class)),
                        userSearchViewConverter,
                        vm
                ),
                viewModel
        );

        final EditBoardScene.Factory editBoardSceneFactory = context -> new EditBoardScene(
                inflater,
                editBoardFeatureFactory,
                stageTitleResolver,
                context
        );

        final SplashScreenScene.Factory splashScreenFactory = SplashScreenScene::new;
        final var loginFactoryProvider = (jakarta.inject.Provider<LoginScene.Factory>) () -> mock(LoginScene.Factory.class);
        final var exceptionFactoryProvider = (jakarta.inject.Provider<ExceptionScene.Factory>) () -> mock(ExceptionScene.Factory.class);
        final LoginService.Factory loginStageContextFactory = url -> new LoginService(
                storeLogger,
                mock(it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase.class),
                url
        );

        final var manager = new EditBoardStage(
                inflater,
                stage,
                themeService,
                splashScreenFactory,
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                editBoardSceneFactory,
                stageContextFactory,
                boardArgResolver,
                mock(HostServices.class),
                new BuildConfig(URI.create("https://example.com/help-uri")),
                new BoardRawArgs.ExplicitBoard(accountId, boardId)
        );
        manager.initialize();
    }

    @Test
    void testEditBoardSceneIsShown(FxRobot robot) throws IOException {
        ScreenshotUtil.captureScene(robot, "EditBoard");
    }
}
