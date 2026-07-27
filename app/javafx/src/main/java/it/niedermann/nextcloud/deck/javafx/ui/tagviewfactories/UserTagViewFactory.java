package it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories;

import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.UserChip;
import it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.UserSearchViewConverter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.scene.Node;
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
        final var chip = new UserChip();
        chip.bind(user, userSearchViewConverter);
        return chip;
    }
}
