package it.niedermann.nextcloud.deck.javafx.ui.shared.views;

import it.niedermann.nextcloud.deck.domain.model.User;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * A view that renders a list of {@link User.ID}s as overlapping {@link AvatarView}s.
 */
public class MultiAvatarView extends HBox {

    private final DoubleProperty avatarSize = new SimpleDoubleProperty(this, "avatarSize", 24.0);

    public MultiAvatarView() {
        avatarSize.addListener((_, _, newValue) -> {
            updateSpacing(newValue.doubleValue());
            for (final var node : getChildren()) {
                if (node instanceof AvatarView avatarView) {
                    avatarView.setFitWidth(newValue.doubleValue());
                    avatarView.setFitHeight(newValue.doubleValue());
                }
            }
        });
        updateSpacing(avatarSize.get());
    }

    private void updateSpacing(double size) {
        setSpacing(-size * 0.3);
    }

    /**
     * Binds the view to a list of user IDs.
     *
     * @param userIds The list of user IDs to display.
     */
    public void bind(List<User.ID> userIds) {
        getChildren().clear();
        if (userIds == null) {
            return;
        }
        for (final var userId : userIds) {
            final var avatarView = new AvatarView();
            avatarView.setFitWidth(avatarSize.get());
            avatarView.setFitHeight(avatarSize.get());
            avatarView.setAvatar(userId);
            getChildren().add(avatarView);
        }
    }

    public DoubleProperty avatarSizeProperty() {
        return avatarSize;
    }

    public double getAvatarSize() {
        return avatarSize.get();
    }

    public void setAvatarSize(double avatarSize) {
        this.avatarSize.set(avatarSize);
    }
}
