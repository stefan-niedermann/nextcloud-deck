package it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories;

import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.AvatarView;
import it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.UserSearchViewConverter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Callback;

@Singleton
public class UserTagViewFactory implements Callback<User, Node> {

    private final UserSearchViewConverter userSearchViewConverter;

    @Inject
    public UserTagViewFactory(UserSearchViewConverter userSearchViewConverter) {
        this.userSearchViewConverter = userSearchViewConverter;
    }

    @Override
    public Node call(User user) {
        Label labelNode = new Label();
        final var avatar = new AvatarView();
        avatar.setUserId(user.id());
        labelNode.setGraphic(avatar);
        labelNode.setText(userSearchViewConverter.toString(user));
        return labelNode;
    }
}
