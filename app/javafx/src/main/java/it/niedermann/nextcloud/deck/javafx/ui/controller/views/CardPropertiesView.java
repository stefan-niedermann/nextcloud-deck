package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.FxUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class CardPropertiesView extends HBox {

    @FXML
    private IconCounterView descriptionIconCounter;
    @FXML
    private IconCounterView labelsIconCounter;
    @FXML
    private IconCounterView commentsIconCounter;
    @FXML
    private IconCounterView attachmentsIconCounter;
    @FXML
    private IconCounterView assigneesIconCounter;

    private final ObjectProperty<Args> args = new SimpleObjectProperty<>(this, "args");

    public CardPropertiesView() {
        Inflater.getInstance().inflate(this);

        final var views = new IconCounterView[]{
                descriptionIconCounter,
                labelsIconCounter,
                commentsIconCounter,
                attachmentsIconCounter,
                assigneesIconCounter,
        };

        managedProperty().bind(visibleProperty());
        FxUtils.anyVisible(views).subscribe(this::setVisible);

        for (final var view : views) {
            view.managedProperty().bind(view.visibleProperty());
        }

        args.subscribe(args -> {
            if (args == null) {
                for (final var view : views) {
                    view.setVisible(false);
                    return;
                }
            }

            descriptionIconCounter.setVisible(args.description() != null && !args.description().isEmpty());
            labelsIconCounter.setVisible(args.labels() > 0);
            commentsIconCounter.setVisible(args.commentsTotalCount() > 0);
            attachmentsIconCounter.setVisible(args.attachments() > 0);
            assigneesIconCounter.setVisible(args.assignees() > 0);

            // TODO Set checkbox item count
            descriptionIconCounter.setCounter(0);
            labelsIconCounter.setCounter(args.labels());
            commentsIconCounter.setCounter(args.commentsTotalCount());
            attachmentsIconCounter.setCounter(args.attachments());
            assigneesIconCounter.setCounter(args.assignees());
        });
    }

    public final ObjectProperty<Args> argsProperty() {
        return args;
    }

    public final Args getArgs() {
        return args.get();
    }

    public final void setArgs(Args value) {
        args.set(value);
    }

    public record Args(String description,
                       int labels,
                       int commentsUnreadCount,
                       int commentsTotalCount,
                       int attachments,
                       int assignees) {
    }
}
