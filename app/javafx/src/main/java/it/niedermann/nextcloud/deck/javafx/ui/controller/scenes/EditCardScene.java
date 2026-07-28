package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditCardStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class EditCardScene extends DisposableController {

    @FXML
    Pane contentHost;

    private final Inflater inflater;
    private final EditCardFeature.Factory editCardFeatureFactory;
    private final EditCardStageContext editCardStageContext;

    @AssistedInject
    public EditCardScene(Inflater inflater,
                          EditCardFeature.Factory editCardFeatureFactory,
                          @Assisted EditCardStageContext editCardStageContext) {
        this.inflater = inflater;
        this.editCardFeatureFactory = editCardFeatureFactory;
        this.editCardStageContext = editCardStageContext;
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
}
