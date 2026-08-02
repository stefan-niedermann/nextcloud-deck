package it.niedermann.nextcloud.deck.domain.usecases.users;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Avatar;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class GetAvatarUseCase {

    private final UserRepository userRepository;
    private final Map<CacheKey, CompletableFuture<Avatar>> cache = new ConcurrentHashMap<>();

    @Inject
    public GetAvatarUseCase(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public CompletableFuture<Avatar> execute(Account account, User.ID userId, int sizeInPx) {
        if (account == null || userId == null) {
            final var result = new CompletableFuture<Avatar>();
            result.completeExceptionally(new IllegalArgumentException("Account and User.ID must not be null"));
            return result;
        }
        final var key = new CacheKey(account.id(), userId, sizeInPx);
        return cache.computeIfAbsent(key, k -> userRepository.getAvatar(account, userId, sizeInPx));
    }

    public CompletableFuture<Avatar> execute(Account account, int sizeInPx) {
        if (account == null) {
            final var result = new CompletableFuture<Avatar>();
            result.completeExceptionally(new IllegalArgumentException("Account must not be null"));
            return result;
        }
        return userRepository.getAvatar(account, sizeInPx);
    }

    public CompletableFuture<Avatar> execute(User.ID userId, int sizeInPx) {
        if (userId == null) {
            final var result = new CompletableFuture<Avatar>();
            result.completeExceptionally(new IllegalArgumentException("User.ID must not be null"));
            return result;
        }
        return userRepository.getAvatar(userId, sizeInPx);
    }

    private record CacheKey(Account.ID accountId, User.ID userId, int sizeInPx) {
    }
}
