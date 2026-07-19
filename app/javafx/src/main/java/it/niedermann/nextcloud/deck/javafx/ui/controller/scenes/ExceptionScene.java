package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.ExceptionDialog;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.EmptyContentView;
import javafx.fxml.FXML;

public class ExceptionScene extends DisposableController {

    private final ExceptionDialog.Factory exceptionDialogFactory;
    private final Throwable exception;

    @FXML
    EmptyContentView emptyContentView;

    @AssistedInject
    public ExceptionScene(ExceptionDialog.Factory exceptionDialogFactory,
                          @Assisted Throwable exception) {
        this.exceptionDialogFactory = exceptionDialogFactory;
        this.exception = exception;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        this.emptyContentView.setOnAction(event -> {
            exceptionDialogFactory.create(exception).show();
            event.consume();
        });
    }

    @AssistedFactory
    public interface Factory {
        ExceptionScene create(Throwable exception);
    }
}
