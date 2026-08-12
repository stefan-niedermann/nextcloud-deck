package it.niedermann.nextcloud.deck.domain.usecases.users;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class ListUsersUseCase {
    private final UserRepository userRepository;

    @Inject
    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flow.Publisher<List<User>> execute(Account.ID accountId) {
        return userRepository.getNotDeletedUsers(accountId);
    }
}
