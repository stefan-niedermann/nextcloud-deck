package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

public class SubmitTextField extends StackPane {

    @FXML
    TextField content;
    @FXML
    Button submitButton;
    @FXML
    Label submitLabel;

    public SubmitTextField() {
        Inflater.getInstance().inflateAndBind(this);
    }

    public void setOnSubmit(Consumer<String> eventHandler) {
        submitButton.setOnAction(event -> {
            eventHandler.accept(getContent());
            event.consume();
        });

        content.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                eventHandler.accept(getContent());
                event.consume();
            }
        });
    }

    @Override
    public void requestFocus() {
        content.requestFocus();
    }

    public void setContent(String value) {
        this.content.setText(value);
    }

    public String getContent() {
        return this.content.getText();
    }

    public StringProperty contentProperty() {
        return this.content.textProperty();
    }

    public void setPromptText(String value) {
        this.content.setPromptText(value);
    }

    public String getPromptText() {
        return this.content.getPromptText();
    }

    public StringProperty promptTextProperty() {
        return this.content.promptTextProperty();
    }

    public void setButtonText(String value) {
        this.submitLabel.setText(value);
    }

    public String getButtonText() {
        return this.submitLabel.getText();
    }

    public StringProperty buttonTextProperty() {
        return this.submitLabel.textProperty();
    }
}
