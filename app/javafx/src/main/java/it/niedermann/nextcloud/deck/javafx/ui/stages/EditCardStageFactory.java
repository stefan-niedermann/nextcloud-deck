package it.niedermann.nextcloud.deck.javafx.ui.stages;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Inject;

public class EditCardStageFactory implements StageManager.StageFactory<Card.ID> {

    @Inject
    public EditCardStageFactory() {

    }

    @Override
    public Inflater.FxBundle<?> inflateContent(Card.ID initialState) {
        // TODO Mock implementation
        throw new UnsupportedOperationException();
    }
}
