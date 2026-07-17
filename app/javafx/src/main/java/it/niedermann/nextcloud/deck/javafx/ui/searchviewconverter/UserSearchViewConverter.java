package it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter;

import it.niedermann.nextcloud.deck.domain.model.User;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.util.StringConverter;

@Singleton
public class UserSearchViewConverter extends StringConverter<User> {

    @Inject
    public UserSearchViewConverter() {

    }

    @Override
    public String toString(User user) {
        if (user == null) {
            return "";
        }

        if (user.displayName().isBlank()) {
            return user.id().value();
        }

        return user.displayName();
    }

    @Override
    public User fromString(String string) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
