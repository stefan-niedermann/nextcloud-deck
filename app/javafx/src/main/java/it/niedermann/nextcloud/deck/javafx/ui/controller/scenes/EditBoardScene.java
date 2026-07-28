package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditBoardStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditBoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class EditBoardScene extends DisposableController {

    @FXML
    Pane contentHost;

    private final Inflater inflater;
    private final EditBoardFeature.Factory editBoardFeatureFactory;
    private final EditBoardStageContext editBoardStageContext;

    @AssistedInject
    public EditBoardScene(Inflater inflater,
                          EditBoardFeature.Factory editBoardFeatureFactory,
                          @Assisted EditBoardStageContext editBoardStageContext) {
        this.inflater = inflater;
        this.editBoardFeatureFactory = editBoardFeatureFactory;
        this.editBoardStageContext = editBoardStageContext;
    }

    @AssistedFactory
    public interface Factory {
        EditBoardScene create(EditBoardStageContext editBoardStageContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var bundle = inflater.inflate(editBoardFeatureFactory.create(editBoardStageContext));
        contentHost.getChildren().add(bundle.view());
    }
}
