package it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories;

import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.LabelSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.util.FxUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.scene.Node;
import javafx.util.Callback;

@Singleton
public class LabelTagViewFactory implements Callback<Label, Node> {

    private final LabelSearchViewConverter labelSearchViewConverter;

    @Inject
    public LabelTagViewFactory(LabelSearchViewConverter labelSearchViewConverter) {
        this.labelSearchViewConverter = labelSearchViewConverter;
    }

    @Override
    public Node call(Label label) {
        javafx.scene.control.Label labelNode = new javafx.scene.control.Label();
        labelNode.setText(labelSearchViewConverter.toString(label));
        labelNode.setBackground(FxUtils.colorToBackground(label.color()));
        return labelNode;
    }
}
