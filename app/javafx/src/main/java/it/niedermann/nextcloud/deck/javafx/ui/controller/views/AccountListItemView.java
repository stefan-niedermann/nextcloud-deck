package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class AccountListItemView extends HBox {

    @FXML
    AvatarView avatar;
    @FXML
    Circle current;
    @FXML
    Label title;

    public AccountListItemView() {
        Inflater.getInstance().inflate(this);
    }

    public void bind(Account account, boolean isCurrent) {
        avatar.setAvatar(account);
        current.setVisible(isCurrent);
        title.setText(account.username());
    }
}
