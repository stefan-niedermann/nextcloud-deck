package it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.usecases.labels.SearchLabelsUseCase;
import jakarta.inject.Inject;
import javafx.util.Callback;

public class LabelSuggestionProvider implements Callback<SearchField.SearchFieldSuggestionRequest, Collection<Label>> {

    private final SearchLabelsUseCase searchLabelsUseCase;
    private Board.ID boardId;

    @Inject
    public LabelSuggestionProvider(
            SearchLabelsUseCase searchLabelsUseCase
    ) {
        this.searchLabelsUseCase = searchLabelsUseCase;
    }

    public void setBoardId(Board.ID boardId) {
        this.boardId = boardId;
    }

    @Override
    public Collection<Label> call(SearchField.SearchFieldSuggestionRequest param) {
        if (boardId == null) {
            return Maybe.fromPublisher(searchLabelsUseCase.execute(param.getUserText())).blockingGet();
        }
        return Maybe.fromPublisher(searchLabelsUseCase.execute(boardId, param.getUserText())).blockingGet();
    }
}
