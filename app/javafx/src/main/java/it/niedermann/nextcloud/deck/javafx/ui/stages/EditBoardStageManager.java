package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.exception.ExceptionUnwrapper;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditBoardStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.EditBoardScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Provider;
import javafx.stage.Stage;

public class EditBoardStageManager extends StageManager<BoardRawArgs> {

    private static final Logger logger = Logger.getLogger(EditBoardStageManager.class.getName());

    private final EditBoardScene.Factory editBoardSceneFactory;
    private final EditBoardStageContext.Factory stageContextFactory;
    private final ArgsResolver<BoardRawArgs, BoardParsedArgs> boardArgResolver;
    private final ExceptionUnwrapper exceptionUnwrapper;

    @AssistedInject
    public EditBoardStageManager(Inflater inflater,
                                 Stage stage,
                                 ThemeService themeService,
                                 SplashScreenScene.Factory splashScreenFactory,
                                 HasAccountsUseCase hasAccountsUseCase,
                                 Provider<LoginScene.Factory> loginFactoryProvider,
                                 Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                                 SetCurrentAccountUseCase setCurrentAccountUseCase,
                                 EditBoardScene.Factory editBoardSceneFactory,
                                 EditBoardStageContext.Factory stageContextFactory,
                                 ExceptionUnwrapper exceptionUnwrapper,
                                 BoardArgResolver boardArgResolver,
                                 @Assisted BoardRawArgs args) {
        super(stage,
                themeService,
                inflater,
                splashScreenFactory,
                hasAccountsUseCase,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                args);
        this.editBoardSceneFactory = editBoardSceneFactory;
        this.stageContextFactory = stageContextFactory;
        this.boardArgResolver = boardArgResolver;
        this.exceptionUnwrapper = exceptionUnwrapper;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        EditBoardStageManager create(BoardRawArgs args);
    }

    @Override
    protected CompletableFuture<Void> showContent(BoardRawArgs rawArgs) {
        return boardArgResolver.resolve(rawArgs)
                .thenApplyAsync(this::inflateContent, JavaFxScheduler.platform().toExecutorService())
                .thenComposeAsync(this::setStageContent)
                .exceptionallyComposeAsync(this::recoverError);
    }

    private Inflater.FxBundle<?> inflateContent(BoardParsedArgs initialState) {

        final var stageContext = stageContextFactory.create(new EditBoardStageContext.State(
                initialState.accountId(),
                initialState.boardId()
        ));

        final var editBoardScene = editBoardSceneFactory.create(stageContext);
        return inflater.inflate(editBoardScene);
    }

    /// @return [CompletableFuture] - completed when the user recovered from the passed throwable
    private CompletableFuture<Void> recoverError(Throwable throwable) {

        logger.log(Level.SEVERE, "Initialization error", throwable);

        return switch (exceptionUnwrapper.unwrap(throwable)) {

            case BoardArgResolver.NoAccountConfiguredException _ ->
                    showLogin().thenComposeAsync(_ -> initialize());

            default -> CompletableFuture.failedFuture(throwable);

        };
    }
}
