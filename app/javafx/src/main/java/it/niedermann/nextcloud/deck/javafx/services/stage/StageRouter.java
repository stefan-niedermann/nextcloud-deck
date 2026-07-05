package it.niedermann.nextcloud.deck.javafx.services.stage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPrimaryStage;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.controller.FeatureFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.SceneController;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

@StageScope
public class StageRouter {

    private final FeatureFactory featureFactory;
    private final Stage stage;
    private final ThemeService themeService;

    private final AtomicReference<Disposable> controller = new AtomicReference<>();

    @Inject
    public StageRouter(FeatureFactory featureFactory,
                       @NamedPrimaryStage Stage stage, // FIXME This should be the generic current stage, not (necessarily) the primary stage
                       ThemeService themeService) {
        this.featureFactory = featureFactory;
        this.stage = stage;
        this.themeService = themeService;

        this.stage.setOnCloseRequest(_ -> {
            final var ctrl = controller.get();
            if (ctrl != null) {
                ctrl.dispose();
            }
        });
    }

    public <T extends SceneController, U> CompletableFuture<U> navigateTo(Class<T> sceneClass) {
        final var cf = new CompletableFuture<U>();

        Platform.runLater(() -> {
            try {
                final var controllerBundle = featureFactory.inflateFeature(sceneClass);
                final var controller = controllerBundle.controller();
                final var oldController = this.controller.getAndSet(controller);

                if (oldController != null && !oldController.isDisposed()) {
                    oldController.dispose();
                }

                final var scene = new Scene(controllerBundle.view());

                themeService.bind(scene);
                stage.setScene(scene);

                if (!stage.isShowing()) {
                    stage.centerOnScreen();
                }

                stage.setOnShown(_ -> cf.complete(null));
                stage.show();

            } catch (UnsupportedOperationException | ClassCastException e) {
                cf.completeExceptionally(e);
            }
        });

        return cf;
    }
}
