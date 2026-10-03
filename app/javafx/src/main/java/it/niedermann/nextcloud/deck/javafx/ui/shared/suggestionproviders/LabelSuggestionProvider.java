package it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.usecases.labels.SearchLabelsUseCase;
import jakarta.inject.Inject;
import javafx.util.Callback;

public class LabelSuggestionProvider implements Callback<SearchField.SearchFieldSuggestionRequest, Collection<Label>> {

    private static final Logger logger = Logger.getLogger(LabelSuggestionProvider.class.getName());

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
        try {
            if (boardId == null) {
                return Maybe.fromPublisher(searchLabelsUseCase.execute(param.getUserText()))
                        .timeout(5, TimeUnit.SECONDS)
                        .blockingGet();
            }
            return Maybe.fromPublisher(searchLabelsUseCase.execute(boardId, param.getUserText()))
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
            logger.log(Level.WARNING, "Failed to fetch label suggestions", e);
            return Collections.emptyList();
        }
    }
}
