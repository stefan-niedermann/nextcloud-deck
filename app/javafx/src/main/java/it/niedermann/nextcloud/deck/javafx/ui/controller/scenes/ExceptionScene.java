package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import it.niedermann.nextcloud.deck.javafx.ui.controller.SceneController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.EmptyContentView;
import jakarta.inject.Inject;
import javafx.fxml.FXML;

public class ExceptionScene extends SceneController {

    @FXML
    EmptyContentView emptyContentView;

    @Inject
    public ExceptionScene() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }
}

