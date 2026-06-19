package it.niedermann.nextcloud.deck.javafx.router;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import it.niedermann.nextcloud.deck.javafx.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.controller.FeatureFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.SceneController;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Router {

    private final FeatureFactory featureFactory;
    private final Stage primaryStage;
    private final ThemeService themeService;

    private final AtomicReference<SceneController> primaryController = new AtomicReference<>();

    @Inject
    public Router(FeatureFactory featureFactory,
                  Stage primaryStage,
                  ThemeService themeService) {
        this.featureFactory = featureFactory;
        this.primaryStage = primaryStage;
        this.themeService = themeService;

        this.primaryStage.setOnCloseRequest(_ -> {
            final var ctrl = primaryController.get();
            if (ctrl != null) {
                ctrl.dispose();
            }
        });
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T extends SceneController> CompletableFuture<Void> navigateTo(RouteConfig<T> routeConfig) {
        return navigateTo(routeConfig, false, primaryStage);
    }

    public <T extends SceneController, U> CompletableFuture<U> openDialog(RouteConfig<T> routeConfig) {
        return navigateTo(routeConfig, true, null);
    }

    public <T extends SceneController, U> CompletableFuture<U> openModal(RouteConfig<T> routeConfig) {
        return navigateTo(routeConfig, false, null);
    }

    private <T extends SceneController, U> CompletableFuture<U> navigateTo(RouteConfig<T> routeConfig, boolean modal, Stage targetStage) {
        final var cf = new CompletableFuture<U>();

        Platform.runLater(() -> {
            try {
                final var controllerBundle = featureFactory.inflateFeature(routeConfig.controllerClass());
                controllerBundle.view().setUserData(routeConfig.routeContext());

                final var controller = controllerBundle.controller();

                final var stage = Optional
                        .ofNullable(targetStage)
                        .orElseGet(Stage::new);

                final var oldController = primaryController.getAndSet(controller);
                if (oldController != null && !oldController.isDisposed()) {
                    oldController.dispose();
                }

                final var scene = new Scene(controllerBundle.view());
                themeService.bind(scene);
                stage.setScene(scene);

                if (!stage.isShowing()) {
                    stage.centerOnScreen();
                }

                if (modal) {
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                } else {
                    stage.show();
                }

                if (controller instanceof ControllerWithResult<?>) {
                    //noinspection unchecked
                    ((ControllerWithResult<U>) controller).getResult().whenComplete((value, error) -> {
                        if (error == null) {
                            cf.complete(value);
                        } else {
                            cf.completeExceptionally(error);
                        }
                    });
                }

            } catch (UnsupportedOperationException | ClassCastException e) {
                cf.completeExceptionally(e);
            }
        });

        return cf;
    }
}
