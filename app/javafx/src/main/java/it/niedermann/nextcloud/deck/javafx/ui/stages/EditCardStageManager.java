package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditCardStageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.LoginStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.EditCardScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Provider;
import javafx.application.Platform;
import javafx.stage.Stage;

public class EditCardStageManager extends StageManager<CardRawArgs, Card.ID> {

    private final EditCardStageContext.Factory editCardStageContextFactory;
    private final EditCardScene.Factory editCardSceneFactory;

    @AssistedInject
    public EditCardStageManager(Inflater inflater,
                                Stage stage,
                                ThemeService themeService,
                                SplashScreenScene.Factory splashScreenFactory,
                                LoginStageContext.Factory loginStageContextFactory,
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
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                cardArgResolver,
                args);
        this.editCardStageContextFactory = editCardStageContextFactory;
        this.editCardSceneFactory = editCardSceneFactory;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        EditCardStageManager create(CardRawArgs args);
    }

    @Override
    protected CompletableFuture<Inflater.FxBundle<Object>> showContent(Card.ID cardId) {
        final var context = editCardStageContextFactory.create(new EditCardStageContext.State(Optional.of(cardId), true), () -> Platform.runLater(stage::close));
        final var scene = editCardSceneFactory.create(context);
        return CompletableFuture.completedFuture(inflater.inflate(scene));
    }
}
