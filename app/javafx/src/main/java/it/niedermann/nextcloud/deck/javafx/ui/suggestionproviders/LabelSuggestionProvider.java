package it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.usecases.labels.SearchLabelsUseCase;
import jakarta.inject.Inject;
import javafx.util.Callback;

public class LabelSuggestionProvider implements Callback<SearchField.SearchFieldSuggestionRequest, Collection<Label>> {

    private final SearchLabelsUseCase searchLabelsUseCase;

    @Inject
    public LabelSuggestionProvider(
            SearchLabelsUseCase searchLabelsUseCase
    ) {
        this.searchLabelsUseCase = searchLabelsUseCase;
    }

    @Override
    public Collection<Label> call(SearchField.SearchFieldSuggestionRequest param) {
        return Maybe.fromPublisher(searchLabelsUseCase.execute(param.getUserText())).blockingGet();
    }
}
