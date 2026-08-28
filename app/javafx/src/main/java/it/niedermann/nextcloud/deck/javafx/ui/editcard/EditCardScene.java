package it.niedermann.nextcloud.deck.javafx.ui.editcard;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.editcard.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class EditCardScene extends AbstractScene {

    @FXML
    Pane contentHost;

    private final EditCardFeature.Factory editCardFeatureFactory;
    private final EditCardService editCardService;
    private final StageTitleResolver stageTitleResolver;

    @AssistedInject
    public EditCardScene(Inflater inflater,
                         EditCardFeature.Factory editCardFeatureFactory,
                         StageTitleResolver stageTitleResolver,
                         ThemeService themeService,
                         HostServices hostServices,
                         BuildConfig buildConfig,
                         SyncScheduler syncScheduler,
                         @Assisted EditCardService editCardService) {
        super(themeService, hostServices, buildConfig, syncScheduler, inflater);

        this.editCardFeatureFactory = editCardFeatureFactory;
        this.editCardService = editCardService;
        this.stageTitleResolver = stageTitleResolver;
    }

    @AssistedFactory
    public interface Factory {
        EditCardScene create(EditCardService editCardService);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var editCardFeature = editCardFeatureFactory.create(editCardService);
        contentHost.getChildren().add(editCardFeature.getRoot());
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.fromPublisher(editCardService.getState())
                .switchMap(state -> stageTitleResolver.resolve(state.cardId().orElse(null)));
    }
}
