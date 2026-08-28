package it.niedermann.nextcloud.deck.javafx.ui.shared;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public abstract class AbstractScene extends AbstractFeature {

    private Scene scene;
    private final ThemeService themeService;
    private final HostServices hostServices;
    private final BuildConfig buildConfig;
    private final SyncScheduler syncScheduler;

    protected AbstractScene(
            ThemeService themeService,
            HostServices hostServices,
            BuildConfig buildConfig,
            SyncScheduler syncScheduler,
            Inflater inflater
    ) {
        super(inflater);
        this.themeService = themeService;
        this.hostServices = hostServices;
        this.buildConfig = buildConfig;
        this.syncScheduler = syncScheduler;
    }

    protected CompletableFuture<Void> refresh() {
        return syncScheduler.scheduleSynchronization();
    }

    public Scene getScene() {
        if (scene == null) {
            this.scene = new Scene(getRoot());
            themeService.bind(scene);
            this.scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.F1) {
                    hostServices.showDocument(buildConfig.helpUri().toString());
                    event.consume();
                } else if (event.getCode() == KeyCode.F5) {
                    refresh();
                    event.consume();
                } else if (event.getCode() == KeyCode.F11) {
                    final var stage = (Stage) scene.getWindow();
                    stage.setFullScreen(!stage.isFullScreen());
                    event.consume();
                }
            });
        }
        return scene;
    }

    public Flowable<String> getTitle() {
        return Flowable.empty();
    }
}
