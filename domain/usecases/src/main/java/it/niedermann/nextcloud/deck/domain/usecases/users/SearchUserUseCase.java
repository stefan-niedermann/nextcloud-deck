package it.niedermann.nextcloud.deck.domain.usecases.users;

import java.util.Collection;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class SearchUserUseCase {

    private final UserRepository userRepository;

    @Inject
    public SearchUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // TODO Add Board.ID for filtering on database level
    public Flow.Publisher<Collection<User>> execute(String query) {
        return userRepository.find(query);
    }

    /// @return any user available on the server instance of the given [Account.ID] that matches the query.
    public Flow.Publisher<Collection<User>> execute(String query, Account.ID accountId) {
        // TODO Use Account.ID for performing online search
        return userRepository.find(query);
    }
}
