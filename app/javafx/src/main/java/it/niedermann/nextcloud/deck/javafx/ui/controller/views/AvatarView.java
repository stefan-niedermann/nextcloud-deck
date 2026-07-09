package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.User;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AvatarView extends ImageView {

    public void setUser(User user) {
        setImage(new Image("https://placehold.co/320x480", true));
//        setImage(new Image(user.accountId(), true));
    }

    public void setAccount(Account account) {
        setImage(new Image("https://placehold.co/320x480", true));
//        setImage(new Image(account.url().toString(), true));
    }
}
