package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;

public interface IPersistenceAdapter {

    void getBoards(IResponseCallback<List<Board>> responseCallback);
}
