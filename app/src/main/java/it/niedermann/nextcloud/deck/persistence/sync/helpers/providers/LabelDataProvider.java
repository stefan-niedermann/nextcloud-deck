package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class LabelDataProvider implements IDataProvider<Label> {

    private Board board;
    private FullStack stack;
    private FullCard card;

    public LabelDataProvider(Board board, FullStack stack, FullCard card) {
        this.board = board;
        this.stack = stack;
        this.card = card;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Label>> responder) {
        responder.onResponse(card.getLabels());
    }

    @Override
    public Label getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, long remoteId) {
        return dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, remoteId);
    }

    @Override
    public void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        dataBaseAdapter.createLabel(accountId, entity);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        dataBaseAdapter.updateLabel(accountId, entity);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, Label existingEntity, Label entityFromServer) {
        // ain't goin' deeper <3
        return;
    }
}
