package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Provider;
import javafx.stage.Stage;

public class EditBoardStageManager extends StageManager<BoardRawArgs> {

    @AssistedInject
    public EditBoardStageManager(Inflater inflater,
                                 Stage stage,
                                 ThemeService themeService,
                                 SplashScreenScene.Factory splashScreenFactory,
                                 HasAccountsUseCase hasAccountsUseCase,
                                 Provider<LoginScene.Factory> loginFactoryProvider,
                                 Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                                 SetCurrentAccountUseCase setCurrentAccountUseCase,
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
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        EditBoardStageManager create(BoardRawArgs args);
    }

    @Override
    protected CompletableFuture<Void> showContent(BoardRawArgs args) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Not yet implemented."));
    }
}
