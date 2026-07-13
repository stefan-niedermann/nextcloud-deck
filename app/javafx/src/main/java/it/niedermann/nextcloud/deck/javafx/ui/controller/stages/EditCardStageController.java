package it.niedermann.nextcloud.deck.javafx.ui.controller.stages;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.ControllerFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.StageController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Inject;

public class EditCardStageController extends StageController<EditCardStageController.Args, Card.ID> {

    @Inject
    public EditCardStageController(Inflater inflater,
                                   StageRouter stageRouter,
                                   ControllerFactory controllerFactory,
                                   LoginScene.Factory loginFactory,
                                   ExceptionScene.Factory exceptionFactory,
                                   SetCurrentAccountUseCase setCurrentAccountUseCase) {
        super(inflater,
                stageRouter,
                controllerFactory,
                loginFactory,
                exceptionFactory,
                setCurrentAccountUseCase);
    }

    @Override
    protected CompletableFuture<Void> showContent(Card.ID initialState) {
        // TODO Mock implementation
        final var bundle = inflater.inflate(controllerFactory.call(MainScene.class));
        return this.stageRouter.setStageContent(bundle);
    }

    @Override
    protected CompletableFuture<Card.ID> deriveInitialState(Args args) {
        // TODO Mock implementation
        return switch (args) {
            case Args.LocalCard localCard -> CompletableFuture.completedFuture(localCard.cardId());
            default -> throw new UnsupportedOperationException();
        };
    }

    public sealed interface Args {
        record LocalCard(Card.ID cardId) implements Args {
        }

        record RemoteCard(URL url) implements Args {
        }
    }
}
