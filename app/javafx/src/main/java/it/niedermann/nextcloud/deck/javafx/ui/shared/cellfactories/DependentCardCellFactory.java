package it.niedermann.nextcloud.deck.javafx.ui.shared.cellfactories;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.DependentCardView;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class DependentCardCellFactory implements Callback<ListView<Card>, ListCell<Card>> {

    private DependentCardView.DependentCardActionListener listener;

    @Inject
    public DependentCardCellFactory() {
    }

    public void setListener(DependentCardView.DependentCardActionListener listener) {
        this.listener = listener;
    }

    @Override
    public ListCell<Card> call(ListView<Card> listView) {
        return new ListCell<>() {
            final DependentCardView view = new DependentCardView();

            {
                final var totalWidth = Bindings.createDoubleBinding(
                        () -> listView.getWidth()
                                - getPadding().getLeft()
                                - getPadding().getRight()
                                - 2,
                        listView.widthProperty(),
                        paddingProperty());

                view.maxWidthProperty().bind(totalWidth);
            }

            @Override
            protected void updateItem(Card card, boolean empty) {
                super.updateItem(card, empty);
                setText(null);

                if (empty || card == null) {
                    setGraphic(null);
                } else {
                    view.bind(card, listener);
                    setGraphic(view);
                }
            }
        };
    }
}
