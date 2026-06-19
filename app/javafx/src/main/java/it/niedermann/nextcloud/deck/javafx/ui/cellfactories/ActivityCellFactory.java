package it.niedermann.nextcloud.deck.javafx.ui.cellfactories;

import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.ActivityView;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ActivityCellFactory implements Callback<ListView<Activity>, ListCell<Activity>> {

    @Inject
    public ActivityCellFactory() {
    }

    @Override
    public ListCell<Activity> call(ListView<Activity> listView) {
        return new ListCell<>() {

            final ActivityView view = new ActivityView();

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
            protected void updateItem(Activity activity, boolean empty) {
                super.updateItem(activity, empty);
                setText(null);

                if (empty) {

                    setGraphic(null);

                } else {

                    view.bind(activity);
                    setGraphic(view);

                }
            }
        };
    }
}
