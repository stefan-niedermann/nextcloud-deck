package it.niedermann.nextcloud.deck.javafx.services.application;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageComponent;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import javafx.stage.Stage;

@Singleton
public class ApplicationRouter {

    private final Stage primaryStage;
    private final StageComponent.Factory stageComponentFactory;
    private final ThemeService themeService;

    private final AtomicReference<Disposable> controller = new AtomicReference<>();

    @Inject
    public ApplicationRouter(@Named("primary") Stage primaryStage,
                             StageComponent.Factory stageComponentFactory,
                             ThemeService themeService) {
        this.primaryStage = primaryStage;
        this.stageComponentFactory = stageComponentFactory;
        this.themeService = themeService;

        this.primaryStage.setOnCloseRequest(_ -> {
            final var ctrl = controller.get();
            if (ctrl != null) {
                ctrl.dispose();
            }
        });
    }

    public CompletableFuture<Void> initializePrimaryStage() {
        final var initialState = new StageContext.State(Optional.of(1L), Optional.of(1L), Optional.empty());
        return launchMainStage(primaryStage, initialState);
    }

    private CompletableFuture<Void> launchMainStage(Stage stage, StageContext.State initialState) {
        final var stageComponent = stageComponentFactory.create(stage, initialState);
        return stageComponent.getMainStage().initialize();
    }

    private CompletableFuture<Void> launchEditCardStage(long cardId) {
        // TODO find accountId and boardId
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Not yet implemented"));
    }

    private CompletableFuture<Void> launchEditCardStage(URL url, long cardRemoteId) {
        // TODO find accountId and boardId
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Not yet implemented"));
    }

    private CompletableFuture<Void> launchEditCardStage(String accountName, long cardRemoteId) {
        // TODO find accountId and boardId
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Not yet implemented"));
    }
}
