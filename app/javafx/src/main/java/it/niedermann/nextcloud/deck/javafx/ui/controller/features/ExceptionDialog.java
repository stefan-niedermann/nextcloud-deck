package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ExceptionDialog {

    public ExceptionDialog() {

    }

    public void show(Throwable exception) {

        final var sw = new StringWriter();
        final var pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        final var textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(false);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final var expandable = new GridPane();
        expandable.setMaxWidth(Double.MAX_VALUE);

        expandable.add(new Label("Stacktrace:"), 0, 0);
        expandable.add(textArea, 0, 1);

        final var alert = new Alert(Alert.AlertType.ERROR, exception.getLocalizedMessage());

        alert.getDialogPane().setExpandableContent(expandable);
        alert.getDialogPane().setExpanded(true);

        alert.showAndWait();
    }
}
