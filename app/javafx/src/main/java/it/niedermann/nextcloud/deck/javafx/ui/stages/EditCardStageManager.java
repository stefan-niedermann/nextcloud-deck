package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
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

public class EditCardStageManager extends StageManager<CardRawArgs> {

    @AssistedInject
    public EditCardStageManager(Inflater inflater,
                                Stage stage,
                                ThemeService themeService,
                                SplashScreenScene.Factory splashScreenFactory,
                                HasAccountsUseCase hasAccountsUseCase,
                                Provider<LoginScene.Factory> loginFactoryProvider,
                                Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                                SetCurrentAccountUseCase setCurrentAccountUseCase,
                                CardArgResolver cardArgResolver,
                                @Assisted CardRawArgs args) {
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
        EditCardStageManager create(CardRawArgs args);
    }

    @Override
    protected CompletableFuture<Void> showContent(CardRawArgs cardRawArgs) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Not yet implemented."));
    }
}
