package it.niedermann.nextcloud.deck.domain.usecases.users;

import java.util.Collection;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class SearchUserUseCase {

    private final UserRepository userRepository;

    @Inject
    public SearchUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flow.Publisher<Collection<User>> execute(String query) {
        return userRepository.find(query);
    }
}
