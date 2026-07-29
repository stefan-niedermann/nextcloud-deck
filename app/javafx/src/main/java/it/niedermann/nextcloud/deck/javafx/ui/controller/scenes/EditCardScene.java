package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.javafx.services.application.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditCardStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.TitleReportable;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class EditCardScene extends DisposableController implements TitleReportable {

    @FXML
    Pane contentHost;

    private final Inflater inflater;
    private final EditCardFeature.Factory editCardFeatureFactory;
    private final EditCardStageContext editCardStageContext;
    private final StageTitleResolver stageTitleResolver;

    @AssistedInject
    public EditCardScene(Inflater inflater,
                          EditCardFeature.Factory editCardFeatureFactory,
                          StageTitleResolver stageTitleResolver,
                          @Assisted EditCardStageContext editCardStageContext) {
        this.inflater = inflater;
        this.editCardFeatureFactory = editCardFeatureFactory;
        this.editCardStageContext = editCardStageContext;
        this.stageTitleResolver = stageTitleResolver;
    }

    @AssistedFactory
    public interface Factory {
        EditCardScene create(EditCardStageContext editCardStageContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var bundle = inflater.inflate(editCardFeatureFactory.create(editCardStageContext));
        contentHost.getChildren().add(bundle.view());
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.fromPublisher(editCardStageContext.getState())
                .switchMap(state -> stageTitleResolver.resolve(state.cardId().orElse(null)));
    }
}
