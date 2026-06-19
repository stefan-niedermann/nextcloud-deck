package it.niedermann.nextcloud.deck.javafx.ui.cellfactories;

import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.AttachmentView;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class AttachmentCellFactory implements Callback<ListView<Attachment>, ListCell<Attachment>> {

    @Inject
    public AttachmentCellFactory() {
    }

    @Override
    public ListCell<Attachment> call(ListView<Attachment> listView) {
        return new ListCell<>() {

            final AttachmentView view = new AttachmentView();

            {
                final var totalWidth = Bindings.createDoubleBinding(
                        () -> listView.getWidth()
                              - getPadding().getLeft()
                              - getPadding().getRight()
                              // FIXME This magic number is probably needed for some border, otherwise the items cause overflow
                              - 2,
                        listView.widthProperty(),
                        paddingProperty());

                view.maxWidthProperty().bind(totalWidth);
            }

            @Override
            protected void updateItem(Attachment attachment, boolean empty) {
                super.updateItem(attachment, empty);
                setText(null);

                if (empty) {

                    setGraphic(null);

                } else {

                    view.bind(attachment);
                    setGraphic(view);

                }
            }
        };
    }
}
