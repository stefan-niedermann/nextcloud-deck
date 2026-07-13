package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class EmptyContentView extends VBox {

    @FXML
    Label title;
    @FXML
    Label description;
    @FXML
    ImageView imageView;
    @FXML
    Button actionButton;

    public EmptyContentView() {
        Inflater.getInstance().inflate(this);

        title.visibleProperty().bind(title.textProperty().isNotNull());
        title.managedProperty().bind(title.visibleProperty());

        description.visibleProperty().bind(description.textProperty().isNotNull());
        description.managedProperty().bind(description.visibleProperty());

        imageView.visibleProperty().bind(imageView.imageProperty().isNotNull());
        imageView.managedProperty().bind(imageView.visibleProperty());

        actionButton.setText(null);
        actionButton.visibleProperty().bind(actionButton.textProperty().isNotNull());
        actionButton.managedProperty().bind(actionButton.visibleProperty());
    }

    public String getTitle() {
        return title.getText();
    }

    public void setTitle(String value) {
        title.setText(value);
    }

    public StringProperty titleProperty() {
        return title.textProperty();
    }

    public String getDescription() {
        return description.getText();
    }

    public void setDescription(String value) {
        description.setText(value);
    }

    public StringProperty descriptionProperty() {
        return description.textProperty();
    }

    public String getImage() {
        return imageView.getImage().getUrl();
    }

    public void setImage(String value) {
        imageView.setImage(new Image(value));
    }

    public String getActionLabel() {
        return actionButton.getText();
    }

    public void setActionLabel(String value) {
        actionButton.setText(value);
    }

    public StringProperty actionLabelProperty() {
        return actionButton.textProperty();
    }

    public final void setOnAction(EventHandler<ActionEvent> handler) {
        actionButton.setOnAction(handler);
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return actionButton.getOnAction();
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return actionButton.onActionProperty();
    }
}
