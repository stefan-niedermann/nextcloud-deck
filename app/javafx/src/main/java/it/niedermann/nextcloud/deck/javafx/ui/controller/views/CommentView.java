package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;

public class CommentView extends HBox {

    @FXML
    AvatarView avatar;
    @FXML
    Label author;
    @FXML
    Label message;
    @FXML
    Label creationDateTime;
    @FXML
    ContextMenu contextMenu;
    @FXML
    MenuItem edit;
    @FXML
    MenuItem reply;
    @FXML
    MenuItem copy;
    @FXML
    MenuItem delete;

    public CommentView() {
        Inflater.getInstance().inflate(this);

        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    public void bind(Comment comment, CommentActionListener commentActionListener) {
        avatar.setAvatar(comment.author());
        author.setText(comment.author().displayName());
        message.setText(comment.message());

        final var created = comment.created().atZone(ZoneId.systemDefault());
        final var duration = created.toInstant().until(Instant.now());

        if (duration.toDays() < 1) {
            creationDateTime.setText(created.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
        } else if (duration.toDays() < 14) {
            creationDateTime.setText(created.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        } else {
            creationDateTime.setText(created.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        }

        creationDateTime.setAccessibleText(created.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));

        edit.setOnAction(event -> {
            commentActionListener.onEditComment(comment);
            event.consume();
        });

        reply.setOnAction(event -> {
            commentActionListener.onReplyComment(comment);
            event.consume();
        });

        copy.setOnAction(event -> {
            commentActionListener.onCopyComment(comment);
            event.consume();
        });

        delete.setOnAction(event -> {
            commentActionListener.onDeleteComment(comment);
            event.consume();
        });
    }

    public interface CommentActionListener {
        void onEditComment(Comment comment);

        void onReplyComment(Comment comment);

        void onCopyComment(Comment comment);

        void onDeleteComment(Comment comment);
    }
}
