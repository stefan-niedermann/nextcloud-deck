package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class AccountListItemView extends HBox {

    @FXML
    AvatarView avatar;
    @FXML
    Label title;

    public AccountListItemView() {
        Inflater.getInstance().inflateAndBind(this);
    }

    public void bind(Account account, boolean isCurrent) {
        avatar.setAccount(account);
        title.setText((isCurrent ? "☒ " : "☐ ") + account.username());
    }
}
