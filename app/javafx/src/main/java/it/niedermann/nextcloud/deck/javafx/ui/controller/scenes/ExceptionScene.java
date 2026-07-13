package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.EmptyContentView;
import javafx.fxml.FXML;

public class ExceptionScene extends DisposableController {

    private final ViewModel viewModel;

    @FXML
    EmptyContentView emptyContentView;

    @AssistedInject
    public ExceptionScene(@Assisted ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        ExceptionScene create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    public interface ViewModel {

    }
}
