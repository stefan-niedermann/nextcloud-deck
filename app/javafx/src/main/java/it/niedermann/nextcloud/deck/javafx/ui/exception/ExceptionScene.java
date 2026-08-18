package it.niedermann.nextcloud.deck.javafx.ui.exception;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.EmptyContentView;
import javafx.fxml.FXML;

public class ExceptionScene extends AbstractScene {

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

    @Override
    public Flowable<String> getTitle() {
        return Flowable.just("Error");
    }
}
