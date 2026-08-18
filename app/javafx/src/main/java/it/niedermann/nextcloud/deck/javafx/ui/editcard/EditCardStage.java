package it.niedermann.nextcloud.deck.javafx.ui.editcard;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.exception.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractStage;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.splashscreen.SplashScreenScene;
import jakarta.inject.Provider;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;

public class EditCardStage extends AbstractStage<CardRawArgs, Card.ID> {

    private final EditCardService.Factory editCardStageContextFactory;
    private final EditCardScene.Factory editCardSceneFactory;

    @AssistedInject
    public EditCardStage(Inflater inflater,
                         Stage stage,
                         ThemeService themeService,
                         SplashScreenScene.Factory splashScreenFactory,
                         LoginService.Factory loginStageContextFactory,
                         Provider<LoginScene.Factory> loginFactoryProvider,
                         Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                         SetCurrentAccountUseCase setCurrentAccountUseCase,
                         CardArgResolver cardArgResolver,
                         EditCardService.Factory editCardStageContextFactory,
                         EditCardScene.Factory editCardSceneFactory,
                         HostServices hostServices,
                         BuildConfig buildConfig,
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
                hostServices,
                buildConfig,
                args);
        this.editCardStageContextFactory = editCardStageContextFactory;
        this.editCardSceneFactory = editCardSceneFactory;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        EditCardStage create(CardRawArgs args);
    }

    @Override
    protected CompletableFuture<Inflater.FxBundle<AbstractScene>> showContent(Card.ID cardId) {
        final var context = editCardStageContextFactory.create(new EditCardService.State(Optional.of(cardId), true), () -> Platform.runLater(stage::close));
        final AbstractScene scene = editCardSceneFactory.create(context);
        return CompletableFuture.completedFuture(inflater.inflate(scene));
    }
}
