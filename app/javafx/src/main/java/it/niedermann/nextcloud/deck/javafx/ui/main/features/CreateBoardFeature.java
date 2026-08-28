package it.niedermann.nextcloud.deck.javafx.ui.main.features;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.SubmitTextField;
import javafx.fxml.FXML;

public class CreateBoardFeature extends AbstractFeature {

    @FXML
    SubmitTextField submitTextField;

    private final Consumer<String> onBoardCreated;

    @AssistedInject
    public CreateBoardFeature(Inflater inflater,
                              @Assisted Consumer<String> onBoardCreated) {
        super(inflater);

        this.onBoardCreated = onBoardCreated;
    }

    @AssistedFactory
    public interface Factory {
        CreateBoardFeature create(Consumer<String> onBoardCreated);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        submitTextField.setOnSubmit(title -> {
            if (title != null && !title.isBlank()) {
                onBoardCreated.accept(title);
            }
        });
    }
}
