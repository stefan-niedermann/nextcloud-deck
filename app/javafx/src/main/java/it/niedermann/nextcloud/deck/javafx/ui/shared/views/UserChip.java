package it.niedermann.nextcloud.deck.javafx.ui.shared.views;

import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.searchviewconverter.UserSearchViewConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserChip extends Label {

    @FXML
    AvatarView avatar;

    public UserChip() {
        Inflater.getInstance().inflate(this);
    }

    public void bind(User user, UserSearchViewConverter converter) {
        avatar.setAvatar(user.id());
        setText(converter.toString(user));
    }
}
