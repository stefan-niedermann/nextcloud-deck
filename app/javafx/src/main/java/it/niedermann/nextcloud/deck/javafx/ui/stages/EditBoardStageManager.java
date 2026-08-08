package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.exception.ExceptionUnwrapper;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditBoardStageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.LoginStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.EditBoardScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Provider;
import javafx.stage.Stage;

public class EditBoardStageManager extends StageManager<BoardRawArgs, BoardParsedArgs> {

    private final EditBoardScene.Factory editBoardSceneFactory;
    private final EditBoardStageContext.Factory stageContextFactory;
    private final ExceptionUnwrapper exceptionUnwrapper;

    @AssistedInject
    public EditBoardStageManager(Inflater inflater,
                                 Stage stage,
                                 ThemeService themeService,
                                 SplashScreenScene.Factory splashScreenFactory,
                                 HasAccountsUseCase hasAccountsUseCase,
                                 LoginStageContext.Factory loginStageContextFactory,
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
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                boardArgResolver,
                args);
        this.editBoardSceneFactory = editBoardSceneFactory;
        this.stageContextFactory = stageContextFactory;
        this.exceptionUnwrapper = exceptionUnwrapper;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        EditBoardStageManager create(BoardRawArgs args);
    }

    @Override
    protected CompletableFuture<Inflater.FxBundle<Object>> showContent(BoardParsedArgs parsedArgs) {
        final var stageContext = stageContextFactory.create(new EditBoardStageContext.State(
                parsedArgs.accountId(),
                parsedArgs.boardId()
        ));

        final var editBoardScene = editBoardSceneFactory.create(stageContext);
        return CompletableFuture.completedFuture(inflater.inflate(editBoardScene));
    }

    @Override
    protected CompletableFuture<Void> recoverError(Throwable throwable) {
        if (exceptionUnwrapper.unwrap(throwable) instanceof BoardArgResolver.NoAccountConfiguredException) {
            return showLoginScene();
        }
        return CompletableFuture.failedFuture(throwable);
    }
}
