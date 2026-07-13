package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.io.PrintWriter;
import java.io.StringWriter;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.javafx.exception.ExceptionUnwrapper;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ExceptionDialog {

    private final ThemeService themeService;
    private final ExceptionUnwrapper exceptionUnwrapper;
    private final Throwable throwable;

    @AssistedInject
    public ExceptionDialog(
            ThemeService themeService,
            ExceptionUnwrapper exceptionUnwrapper,
            @Assisted Throwable throwable) {
        this.themeService = themeService;
        this.exceptionUnwrapper = exceptionUnwrapper;
        this.throwable = throwable;
    }

    @AssistedFactory
    public interface Factory {
        ExceptionDialog create(Throwable throwable);
    }

    public void show() {
        final var unwrappedException = exceptionUnwrapper.unwrap(throwable);

        final var sw = new StringWriter();
        final var pw = new PrintWriter(sw);
        unwrappedException.printStackTrace(pw);

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

        final var alert = new Alert(Alert.AlertType.ERROR, unwrappedException.getLocalizedMessage());

        themeService.bind(alert);
        alert.getDialogPane().setExpandableContent(expandable);
        alert.getDialogPane().setExpanded(true);

        alert.showAndWait();
    }
}
