package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.SyncStatus;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

/// Combining an [AvatarView] with a [javafx.scene.control.ProgressIndicator], e.g. for import or sync status
public class AvatarProgressView extends StackPane {

    private final Logger logger = Logger.getLogger(AvatarProgressView.class.getName());

    @FXML
    AvatarView avatar;
    @FXML
    ProgressIndicator progress;

    public AvatarProgressView() {
        Inflater.getInstance().inflate(this);

        final var sizeBinding = Bindings.createDoubleBinding(
                () -> Math.min(getWidth(), getHeight()),
                widthProperty(),
                heightProperty()
        );

        progress.minWidthProperty().bind(sizeBinding);
        progress.minHeightProperty().bind(sizeBinding);
        progress.prefWidthProperty().bind(sizeBinding);
        progress.prefHeightProperty().bind(sizeBinding);
        progress.maxWidthProperty().bind(sizeBinding);
        progress.maxHeightProperty().bind(sizeBinding);

        final var avatarSizeBinding = sizeBinding.multiply(0.8);
        avatar.fitWidthProperty().bind(avatarSizeBinding);
        avatar.fitHeightProperty().bind(avatarSizeBinding);
    }

    @Override
    protected double computePrefWidth(double height) {
        return 32;
    }

    @Override
    protected double computePrefHeight(double width) {
        return 32;
    }

    @Override
    protected double computeMinWidth(double height) {
        return 16;
    }

    @Override
    protected double computeMinHeight(double width) {
        return 16;
    }

    public void setAvatar(Account account) {
        avatar.setAvatar(account);
    }

    public void setSyncStatus(SyncStatus syncStatus) {

        avatar.setAvatar(syncStatus.account());

        if (syncStatus.boardsFinishedCount() == 0) {

            this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            this.progress.setVisible(true);
            this.progress.setDisable(false);

        } else if (syncStatus.boardsFinishedCount() > 0) {

            this.progress.setProgress(Math.min(1, (double) syncStatus.boardsFinishedCount() / syncStatus.boardsTotalCount()));

            if (syncStatus.boardsFinishedCount() == syncStatus.boardsTotalCount()) {
                this.progress.setVisible(false);
                this.progress.setDisable(true);
                this.progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            }
        }
    }
}
