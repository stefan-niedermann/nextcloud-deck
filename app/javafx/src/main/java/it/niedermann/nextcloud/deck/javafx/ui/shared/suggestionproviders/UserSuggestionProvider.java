package it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.users.SearchUserUseCase;
import jakarta.inject.Inject;
import javafx.util.Callback;

public class UserSuggestionProvider implements Callback<SearchField.SearchFieldSuggestionRequest, Collection<User>> {

    private static final Logger logger = Logger.getLogger(UserSuggestionProvider.class.getName());

    private final SearchUserUseCase searchUserUseCase;

    @Inject
    public UserSuggestionProvider(
            SearchUserUseCase searchUserUseCase
    ) {
        this.searchUserUseCase = searchUserUseCase;
    }

    @Override
    public Collection<User> call(SearchField.SearchFieldSuggestionRequest param) {
        try {
            return Maybe.fromPublisher(searchUserUseCase.execute(param.getUserText()))
                    .timeout(5, TimeUnit.SECONDS)
                    .blockingGet();
        } catch (Exception e) {
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof InterruptedException) {
                    return Collections.emptyList();
                }
                cause = cause.getCause();
            }
            logger.log(Level.WARNING, "Failed to fetch user suggestions", e);
            return Collections.emptyList();
        }
    }
}
