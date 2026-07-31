package it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories;

import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.LabelView;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.scene.Node;
import javafx.util.Callback;

@Singleton
public class LabelTagViewFactory implements Callback<Label, Node> {

    private final ColorUtil colorUtil;

    @Inject
    public LabelTagViewFactory(ColorUtil colorUtil) {
        this.colorUtil = colorUtil;
    }

    @Override
    public Node call(Label label) {
        final var labelView = new LabelView(colorUtil);
        labelView.setLabel(label.title(), label.color());
        return labelView;
    }
}
