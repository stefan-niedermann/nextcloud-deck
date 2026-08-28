package it.niedermann.nextcloud.deck.javafx.ui.editboard;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.editboard.features.EditBoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class EditBoardScene extends AbstractScene {

    @FXML
    Pane contentHost;

    private final EditBoardFeature.Factory editBoardFeatureFactory;
    private final EditBoardService editBoardService;
    private final StageTitleResolver stageTitleResolver;

    @AssistedInject
    public EditBoardScene(Inflater inflater,
                          EditBoardFeature.Factory editBoardFeatureFactory,
                          StageTitleResolver stageTitleResolver,
                          ThemeService themeService,
                          HostServices hostServices,
                          BuildConfig buildConfig,
                          SyncScheduler syncScheduler,
                          @Assisted EditBoardService editBoardService) {
        super(themeService, hostServices, buildConfig, syncScheduler, inflater);

        this.editBoardFeatureFactory = editBoardFeatureFactory;
        this.editBoardService = editBoardService;
        this.stageTitleResolver = stageTitleResolver;
    }

    @AssistedFactory
    public interface Factory {
        EditBoardScene create(EditBoardService editBoardService);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var editBoardFeature = editBoardFeatureFactory.create(editBoardService);
        contentHost.getChildren().add(editBoardFeature.getRoot());
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.fromPublisher(editBoardService.getState())
                .switchMap(state -> stageTitleResolver.resolve(state.accountId(), state.boardId()));
    }
}
