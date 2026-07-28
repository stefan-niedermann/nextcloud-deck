package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditCardStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.EditCardScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Provider;
import javafx.application.Platform;
import javafx.stage.Stage;

public class EditCardStageManager extends StageManager<CardRawArgs> {

    private final Stage stage;
    private final CardArgResolver cardArgResolver;
    private final EditCardStageContext.Factory editCardStageContextFactory;
    private final EditCardScene.Factory editCardSceneFactory;

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
                                EditCardStageContext.Factory editCardStageContextFactory,
                                EditCardScene.Factory editCardSceneFactory,
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
        this.stage = stage;
        this.cardArgResolver = cardArgResolver;
        this.editCardStageContextFactory = editCardStageContextFactory;
        this.editCardSceneFactory = editCardSceneFactory;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        EditCardStageManager create(CardRawArgs args);
    }

    @Override
    protected CompletableFuture<Void> showContent(CardRawArgs cardRawArgs) {
        return cardArgResolver.resolve(cardRawArgs)
                .thenApplyAsync(this::inflateContent, JavaFxScheduler.platform().toExecutorService())
                .thenComposeAsync(this::setStageContent);
    }

    private Inflater.FxBundle<?> inflateContent(Card.ID cardId) {
        final var context = editCardStageContextFactory.create(new EditCardStageContext.State(Optional.of(cardId), true), () -> Platform.runLater(stage::close));
        final var scene = editCardSceneFactory.create(context);
        return inflater.inflate(scene);
    }
}
