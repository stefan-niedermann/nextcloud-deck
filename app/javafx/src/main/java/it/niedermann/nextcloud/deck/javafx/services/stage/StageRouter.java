package it.niedermann.nextcloud.deck.javafx.services.stage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPrimaryStage;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

@StageScope
public class StageRouter {

    private final Stage stage;
    private final ThemeService themeService;

    private final AtomicReference<Object> controller = new AtomicReference<>();

    @Inject
    public StageRouter(@NamedPrimaryStage Stage stage, // FIXME This should be the generic current stage, not (necessarily) the primary stage
                       ThemeService themeService) {
        this.stage = stage;
        this.themeService = themeService;

        this.stage.setOnCloseRequest(_ -> {
            final var ctrl = controller.get();
            if (ctrl instanceof Disposable disposableCtrl) {
                disposableCtrl.dispose();
            }
        });
    }

    public <T, U> CompletableFuture<U> setStageContent(Inflater.FxBundle<T> controllerBundle) {
        final var cf = new CompletableFuture<U>();

        Platform.runLater(() -> {
            try {
                final var controller = controllerBundle.controller();
                final var oldCtrl = this.controller.getAndSet(controller);

                if (oldCtrl instanceof Disposable oldDisposableCtrl && !oldDisposableCtrl.isDisposed()) {
                    oldDisposableCtrl.dispose();
                }

                final var scene = new Scene(controllerBundle.view());

                themeService.bind(scene);
                stage.setScene(scene);

                if (!stage.isShowing()) {
                    stage.centerOnScreen();
                }

                if (stage.isShowing()) {
                    cf.complete(null);
                } else {
                    stage.setOnShown(_ -> cf.complete(null));
                    stage.show();
                }

            } catch (UnsupportedOperationException | ClassCastException e) {
                cf.completeExceptionally(e);
            }
        });

        return cf;
    }
}
