package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.deck.domain.model.query.PreviewActivity;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ActivityView extends HBox {

    @FXML
    AvatarView avatar;
    @FXML
    Label author;
    @FXML
    Label message;
    @FXML
    Label creationDateTime;

    public ActivityView() {
        Inflater.getInstance().inflate(this);
    }

    public void bind(PreviewActivity preview) {
        final var activity = preview.activity();
        avatar.setAvatar(preview.account(), activity.author().id());
        author.setText(activity.author().displayName());
        message.setText(activity.subject());

        final var created = activity.createdAt().atZoneSameInstant(ZoneId.systemDefault());
        final var duration = Duration.between(activity.createdAt(), OffsetDateTime.now());

        if (duration.toDays() < 1) {
            creationDateTime.setText(created.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
        } else if (duration.toDays() < 14) {
            creationDateTime.setText(created.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        } else {
            creationDateTime.setText(created.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        }

        creationDateTime.setAccessibleText(created.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));

    }
}
