package it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;
import javafx.util.Callback;

public class UserSuggestionProvider implements Callback<SearchField.SearchFieldSuggestionRequest, Collection<User>> {

    private final UserRepository userRepository;

    @Inject
    public UserSuggestionProvider(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<User> call(SearchField.SearchFieldSuggestionRequest param) {
        return Maybe.fromPublisher(userRepository.find(param.getUserText())).blockingGet();
    }
}
