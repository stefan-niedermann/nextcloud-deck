package it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import jakarta.inject.Inject;
import javafx.util.Callback;

public class LabelSuggestionProvider implements Callback<SearchField.SearchFieldSuggestionRequest, Collection<Label>> {

    private final LabelRepository labelRepository;

    @Inject
    public LabelSuggestionProvider(
            LabelRepository labelRepository
    ) {
        this.labelRepository = labelRepository;
    }

    @Override
    public Collection<Label> call(SearchField.SearchFieldSuggestionRequest param) {
        return Maybe.fromPublisher(labelRepository.find(param.getUserText())).blockingGet();
    }
}
