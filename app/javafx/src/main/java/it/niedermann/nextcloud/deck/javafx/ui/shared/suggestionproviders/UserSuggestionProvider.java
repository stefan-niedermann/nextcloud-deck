package it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.users.SearchUserUseCase;
import jakarta.inject.Inject;
import javafx.util.Callback;

public class UserSuggestionProvider implements Callback<SearchField.SearchFieldSuggestionRequest, Collection<User>> {

    private final SearchUserUseCase searchUserUseCase;

    @Inject
    public UserSuggestionProvider(
            SearchUserUseCase searchUserUseCase
    ) {
        this.searchUserUseCase = searchUserUseCase;
    }

    @Override
    public Collection<User> call(SearchField.SearchFieldSuggestionRequest param) {
        return Maybe.fromPublisher(searchUserUseCase.execute(param.getUserText())).blockingGet();
    }
}
