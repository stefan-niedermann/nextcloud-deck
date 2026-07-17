package it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter;

import it.niedermann.nextcloud.deck.domain.model.Label;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.util.StringConverter;

@Singleton
public class LabelSearchViewConverter extends StringConverter<Label> {

    @Inject
    public LabelSearchViewConverter() {

    }

    @Override
    public String toString(Label label) {
        if (label == null) {
            return "";
        }

        return label.title();
    }

    @Override
    public Label fromString(String string) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
