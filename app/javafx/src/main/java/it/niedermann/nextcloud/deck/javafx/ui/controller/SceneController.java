package it.niedermann.nextcloud.deck.javafx.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;

public abstract class SceneController extends DisposableController {

//    protected Scene scene;
//    protected Stage stage;

    @FXML
    protected Parent root;
//
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        super.initialize(location, resources);
//
//        Platform.runLater(() -> {
//            this.scene = root.getScene();
//            this.stage = ((Stage) scene.getWindow());
//
//            final var title = resources.getString(getDefaultTitleKey());
//            setTitle(title);
//        });
//    }
//
//    protected void setTitle(String title) {
//        if (stage == null) {
//            throw new IllegalStateException("FXML root node has not been bound yet. This method must be called with Platform.runLater()");
//        }
//
//        stage.setTitle(title);
//    }
//
//    protected String getDefaultTitleKey() {
//        return "title";
//    }

}
