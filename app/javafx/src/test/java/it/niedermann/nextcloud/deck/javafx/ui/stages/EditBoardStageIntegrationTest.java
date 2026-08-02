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

import java.util.concurrent.CompletableFuture;

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
import it.niedermann.nextcloud.deck.javafx.exception.ExceptionUnwrapper;
import it.niedermann.nextcloud.deck.javafx.services.application.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditBoardStageContext;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardFeature;
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
        when(boardArgResolver.resolve(any())).thenReturn(CompletableFuture.completedFuture(boardParsedArgs));
        
        final var storeLogger = mock(StoreLogger.class);
        final var detector = mock(OsThemeDetector.class);
        when(detector.isDark()).thenReturn(false);
        final var keyValueStore = mock(KeyValueStore.class);
        when(keyValueStore.getString(ThemeService.KEY_THEME)).thenReturn(Flowable.empty());
        final var themeService = new ThemeService(detector, keyValueStore);


        final var stageContext = new EditBoardStageContext(
                storeLogger,
                mock(GetBoardUseCase.class),
                mock(ListCardsUseCase.class),
                mock(AddColumnUseCase.class),
                mock(UpdateColumnUseCase.class),
                mock(DeleteColumnUseCase.class),
                mock(ListColumnsUseCase.class),
                mock(GetColumnUseCase.class),
                mock(AddLabelUseCase.class),
                mock(UpdateLabelUseCase.class),
                mock(DeleteLabelUseCase.class),
                mock(ListLabelsUseCase.class),
                mock(ListBoardSharesUseCase.class),
                mock(AddBoardShareUseCase.class),
                mock(RemoveBoardShareUseCase.class),
                mock(UpdateBoardShareUseCase.class),
                new EditBoardStageContext.State(accountId, boardId)
        );
        final EditBoardStageContext.Factory stageContextFactory = initialState -> stageContext;
        
        final var inflater = Inflater.getInstance();
        
        final var getAccountUseCase = mock(GetAccountUseCase.class);
        final var getAccountsUseCase = mock(GetAccountsUseCase.class);
        when(getAccountsUseCase.execute()).thenReturn(Flowable.empty());
        final var getBoardUseCase = mock(GetBoardUseCase.class);
        when(getBoardUseCase.execute(any())).thenReturn(Flowable.empty());
        final var getCardUseCase = mock(GetCardUseCase.class);
        final var getColumnUseCase = mock(GetColumnUseCase.class);
        when(getColumnUseCase.execute(any())).thenReturn(Flowable.empty());
        
        final var stageTitleResolver = new StageTitleResolver(
                getAccountUseCase,
                getAccountsUseCase,
                getBoardUseCase,
                getCardUseCase,
                getColumnUseCase
        );

        final EditBoardScene.Factory editBoardSceneFactory = context -> new EditBoardScene(
                inflater,
                mock(EditBoardFeature.Factory.class, Answers.RETURNS_MOCKS),
                stageTitleResolver,
                context
        );

        final SplashScreenScene.Factory splashScreenFactory = SplashScreenScene::new;
        final var loginFactoryProvider = (jakarta.inject.Provider<LoginScene.Factory>) () -> mock(LoginScene.Factory.class);
        final var exceptionFactoryProvider = (jakarta.inject.Provider<ExceptionScene.Factory>) () -> mock(ExceptionScene.Factory.class);
        final var exceptionUnwrapper = new ExceptionUnwrapper();

        new EditBoardStageManager(
                inflater,
                stage,
                themeService,
                splashScreenFactory,
                hasAccountsUseCase,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                editBoardSceneFactory,
                stageContextFactory,
                exceptionUnwrapper,
                boardArgResolver,
                new BoardRawArgs.ExplicitBoard(accountId, boardId)
        );
    }

    @Test
    void testEditBoardSceneIsShown(FxRobot robot) {
    }
}
