package it.niedermann.nextcloud.deck.javafx.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import it.niedermann.nextcloud.deck.javafx.router.RouteContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class SceneController extends DisposableController {

    protected Scene scene;
    protected Stage stage;

    @FXML
    protected Parent root;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        Platform.runLater(() -> {
            this.scene = root.getScene();
            this.stage = ((Stage) scene.getWindow());

            final var title = resources.getString(getDefaultTitleKey());
            setTitle(title);
        });
    }

    protected RouteContext getContext() {
        return (RouteContext) root.getUserData();
    }

    protected void setTitle(String title) {
        if (stage == null) {
            throw new IllegalStateException("FXML root node has not been bound yet. This method must be called with Platform.runLater()");
        }

        stage.setTitle(title);
    }

    protected String getDefaultTitleKey() {
        return "title";
    }

}
