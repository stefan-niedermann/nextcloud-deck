package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.FileUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class AttachmentView extends HBox {

    @FXML
    ImageView preview;
    @FXML
    Label name;
    @FXML
    Label size;
    @FXML
    Label created;
    @FXML
    ContextMenu contextMenu;
    @FXML
    MenuItem delete;

    public AttachmentView() {
        Inflater.getInstance().inflate(this);

        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    public void bind(Attachment attachment) {
        final var previewImageUrl = attachment
                .localFullPath()
                .or(attachment::localCachePath)
                .map(Path::toUri)
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        return null;
                    }
                })
                .map(URL::toString)
                // TODO Trigger Download?
                .orElseGet(() -> getPreviewImage(attachment.mimetype()));

        preview.setImage(new Image(previewImageUrl, true));
        name.setText(attachment.filename());
        size.setText(FileUtil.humanReadableSize(attachment.fileSize()));

        delete.setOnAction(event -> {
            // TODO Prompt user for "Delete local" vs. "Delete remote"?
//            attachmentActionListener.onDeleteAttachment(attachment);
            event.consume();
        });

//        final var created = attachment.createdAt().atZone(ZoneId.systemDefault());
//        final var duration = created.toInstant().until(Instant.now());
//
//        if (duration.toDays() < 1) {
//            creationDateTime.setText(created.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
//        } else if (duration.toDays() < 14) {
//            creationDateTime.setText(created.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
//        } else {
//            creationDateTime.setText(created.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
//        }
//
//        creationDateTime.setAccessibleText(created.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));

    }

    private String getPreviewImage(String mimeType) {
        // TODO Mock
        return URI.create("https://placehold.co/320x320").toString();
    }
}
