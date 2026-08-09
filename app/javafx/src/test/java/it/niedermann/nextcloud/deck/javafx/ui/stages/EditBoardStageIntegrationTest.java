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

import java.util.concurrent.Flow;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
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
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.UpdateColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.AddLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.DeleteLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.UpdateLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.application.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditBoardStageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.LoginStageContext;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.EditBoardScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
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
        final var listColumnsUseCase = mock(ListColumnsUseCase.class, Answers.RETURNS_MOCKS);
        final var getColumnUseCase = mock(GetColumnUseCase.class, Answers.RETURNS_MOCKS);
        final var listLabelsUseCase = mock(ListLabelsUseCase.class, Answers.RETURNS_MOCKS);
        final var listBoardSharesUseCase = mock(ListBoardSharesUseCase.class, Answers.RETURNS_MOCKS);

        when(getBoardUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listCardsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listColumnsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(getColumnUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listLabelsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listBoardSharesUseCase.execute(any())).thenReturn(Flowable.empty());


        final var stageContext = new EditBoardStageContext(
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
                new EditBoardStageContext.State(accountId, boardId)
        );
        final EditBoardStageContext.Factory stageContextFactory = initialState -> stageContext;
        
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

        final var userSearchViewConverter = new it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.UserSearchViewConverter();
        final it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardFeature.Factory editBoardFeatureFactory = viewModel -> new it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardFeature(
                inflater,
                it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardDetailsFeature::new,
                it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardColumnsFeature::new,
                it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardLabelsFeature::new,
                vm -> new it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardShareFeature(
                        new it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders.UserSuggestionProvider(mock(it.niedermann.nextcloud.deck.domain.usecases.users.SearchUserUseCase.class)),
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
        final LoginStageContext.Factory loginStageContextFactory = url -> new LoginStageContext(
                storeLogger,
                mock(it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase.class),
                url
        );

        final var manager = new EditBoardStageManager(
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
                new BoardRawArgs.ExplicitBoard(accountId, boardId)
        );
        manager.initialize();
    }

    @Test
    void testEditBoardSceneIsShown(FxRobot robot) {
    }
}
